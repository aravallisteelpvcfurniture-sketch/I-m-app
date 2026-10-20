package com.example.ui.viewmodel

import android.app.Activity
import android.content.Context
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
  data class Error(val message: String) : AuthUiState
}

class AuthViewModel(
  private val repository: AravalliRepository
) : ViewModel() {

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
        repository.updateUserRole(current.uid, role)
      }
    }
  }

  fun signInWithGoogle(context: Context) {
    val clientId = try {
      context.getString(R.string.default_web_client_id)
    } catch (e: Exception) {
      _uiState.value = AuthUiState.Error("Google Sign-In configuration missing: default_web_client_id not found")
      return
    }

    _uiState.value = AuthUiState.Loading
    val credentialManager = CredentialManager.create(context)
    val signInOption = GetSignInWithGoogleOption.Builder(serverClientId = clientId).build()
    val request = GetCredentialRequest.Builder().addCredentialOption(signInOption).build()

    viewModelScope.launch {
      try {
        val result = credentialManager.getCredential(context as Activity, request)
        val credential = result.credential
        if (credential is CustomCredential && credential.type == TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
          val googleIdToken = GoogleIdTokenCredential.createFrom(credential.data).idToken
          val authCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
          val authResult = auth.signInWithCredential(authCredential).await()
          val user = authResult.user
          if (user != null) {
            // Check if admin email
            val initialRole = if (user.email == "harishpatelh450@gmail.com") UserRole.ADMIN else _selectedRole.value
            _selectedRole.value = initialRole

            val profile = UserProfile(
              userId = user.uid,
              name = user.displayName ?: "Aravalli Partner",
              email = user.email ?: "",
              phone = user.phoneNumber ?: "",
              role = initialRole.name
            )
            repository.saveUserProfile(profile)
            _uiState.value = AuthUiState.Authenticated(user, profile)
            observeProfile(user.uid)
          } else {
            _uiState.value = AuthUiState.Error("Google Authentication returned null user")
          }
        } else {
          _uiState.value = AuthUiState.Error("Unexpected credential type returned")
        }
      } catch (e: GetCredentialCancellationException) {
        Log.w("Auth", "Google Sign-In cancelled: ${e.message}", e)
        // Reset loading state safely on cancellation
        _uiState.value = AuthUiState.Idle
      } catch (e: Exception) {
        Log.e("Auth", "Google Sign-In failed", e)
        _uiState.value = AuthUiState.Error(e.localizedMessage ?: "Sign-in failed")
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
    val credentialManager = CredentialManager.create(context)
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
