package com.example.ui.viewmodel

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.model.UserProfile
import com.example.data.model.UserRole
import com.example.data.repository.AravalliRepository
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential.Companion.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed interface AuthUiState {
  data object Idle : AuthUiState
  data object Loading : AuthUiState
  data class Authenticated(val user: FirebaseUser, val profile: UserProfile?) : AuthUiState
  data class Error(val message: String, val isConfigError: Boolean = false) : AuthUiState
}

// Extension function to safely extract the Activity from any ContextWrapper in Compose
private tailrec fun Context.findActivity(): Activity? = when (this) {
  is Activity -> this
  is ContextWrapper -> baseContext.findActivity()
  else -> null
}

class AuthViewModel(
  private val repository: AravalliRepository
) : ViewModel() {

  companion object {
    const val APP_PACKAGE_NAME = "Com.aravalli.my"
    const val APP_SHA1 = "9D:63:99:B6:7D:6D:CE:6C:90:A5:46:D8:BE:3C:07:85:30:FC:3B:8E"
    const val APP_SHA256 = "D4:99:F2:B7:36:34:D5:8F:0C:B1:AE:3E:F2:B5:AD:09:3B:98:58:AF:23:4E:A9:44:4A:D0:72:61:3C:F6:2E:83"
    const val FIREBASE_PROJECT_ID = "aravallisteel-60c94"
  }

  private val auth: FirebaseAuth get() = Firebase.auth
  private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
  val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

  private val _selectedRole = MutableStateFlow(UserRole.ADMIN)
  val selectedRole: StateFlow<UserRole> = _selectedRole.asStateFlow()

  private var activeProfile: UserProfile? = null

  init {
    val current = auth.currentUser
    if (current != null) {
      _uiState.value = AuthUiState.Authenticated(current, null)
      observeProfile(current.uid)
    }
  }

  fun setSelectedRole(role: UserRole) {
    _selectedRole.value = role
    val current = auth.currentUser
    if (current != null) {
      viewModelScope.launch {
        try {
          repository.updateUserRole(current.uid, role)
        } catch (e: Exception) {
          Log.w("Auth", "Failed to update role", e)
        }
      }
    }
  }

  fun clearError() {
    _uiState.value = AuthUiState.Idle
  }

  fun signInWithGoogle(context: Context) {
    val activity = context.findActivity()
    if (activity == null) {
      _uiState.value = AuthUiState.Error("Activity context not found. Please restart the app.")
      return
    }

    val clientId = try {
      context.getString(R.string.default_web_client_id)
    } catch (e: Exception) {
      _uiState.value = AuthUiState.Error(
        message = "Google Sign-In configuration missing: default_web_client_id not found in google-services.json",
        isConfigError = true
      )
      return
    }

    _uiState.value = AuthUiState.Loading
    val credentialManager = CredentialManager.create(activity)
    val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
    val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

    viewModelScope.launch {
      try {
        val result = credentialManager.getCredential(activity, request)
        val credential = result.credential
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
          val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
          val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
          val authResult = auth.signInWithCredential(authCredential).await()
          val user = authResult.user
          if (user != null) {
            val initialRole = if (user.email == "harishpatelh450@gmail.com") UserRole.ADMIN else _selectedRole.value
            _selectedRole.value = initialRole

            val profile = UserProfile(
              userId = user.uid,
              name = user.displayName ?: "Aravalli Partner",
              email = user.email ?: "",
              phone = user.phoneNumber ?: "",
              role = initialRole.name
            )
            try {
              repository.saveUserProfile(profile)
            } catch (e: Exception) {
              Log.w("Auth", "Failed to save profile to Firestore", e)
            }
            _uiState.value = AuthUiState.Authenticated(user, profile)
            observeProfile(user.uid)
          } else {
            _uiState.value = AuthUiState.Error("Google Authentication returned null user")
          }
        } else {
          _uiState.value = AuthUiState.Error("Unexpected credential type returned from Google Identity")
        }
      } catch (e: GetCredentialCancellationException) {
        Log.w("Auth", "Google Sign-In cancelled: ${e.message}", e)
        _uiState.value = AuthUiState.Idle
      } catch (e: Exception) {
        Log.e("Auth", "Google Sign-In failed", e)
        val rawMsg = e.localizedMessage ?: e.message ?: "Sign-in failed"
        val isConfigMismatch = rawMsg.contains("16") ||
            rawMsg.contains("Cannot find a matching credential") ||
            rawMsg.contains("Developer Error") ||
            rawMsg.contains("10:") ||
            rawMsg.contains("12500")

        val userMessage = if (isConfigMismatch) {
          "Google Sign-In Error: Firebase Console में SHA-1 Fingerprint या Google Provider सक्षम (Enable) नहीं है।"
        } else if (rawMsg.contains("NoCredentialException")) {
          "डिवाइस पर कोई Google अकाउंट नहीं मिला या Google Play Services उपलब्ध नहीं है।"
        } else {
          "Sign-In Error: $rawMsg"
        }
        _uiState.value = AuthUiState.Error(userMessage, isConfigError = isConfigMismatch)
      }
    }
  }

  private fun observeProfile(uid: String) {
    viewModelScope.launch {
      repository.observeUserProfile(uid)
        .catch { e -> Log.w("Auth", "Could not observe user profile", e) }
        .collect { profile ->
          activeProfile = profile
          if (profile != null) {
            try {
              _selectedRole.value = UserRole.valueOf(profile.role)
            } catch (_: Exception) {}
          }
          val current = auth.currentUser
          if (current != null) {
            _uiState.value = AuthUiState.Authenticated(current, profile)
          }
        }
    }
  }

  fun signOut(context: Context) {
    auth.signOut()
    val activity = context.findActivity() ?: context
    val credentialManager = CredentialManager.create(activity)
    viewModelScope.launch {
      try {
        credentialManager.clearCredentialState(ClearCredentialStateRequest())
      } catch (e: Exception) {
        Log.e("Auth", "Failed to clear credential state", e)
      } finally {
        _uiState.value = AuthUiState.Idle
      }
    }
  }
}
