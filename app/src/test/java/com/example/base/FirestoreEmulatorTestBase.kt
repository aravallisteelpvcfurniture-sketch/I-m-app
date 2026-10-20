package com.example.base

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.MemoryCacheSettings
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Before
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
@LooperMode(LooperMode.Mode.INSTRUMENTATION_TEST)
abstract class FirestoreEmulatorTestBase {

  protected lateinit var firestore: FirebaseFirestore
  protected lateinit var auth: FirebaseAuth
  protected lateinit var databaseId: String

  @Before
  open fun setUpFirebase() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val resId = context.resources.getIdentifier("firestore_database_id", "string", context.packageName)
    databaseId = if (resId != 0) context.getString(resId) else "ai-studio-android-aravalli-e9de20e0-da2e-46c8-8e7b-5860f103b0ba"

    val app = if (FirebaseApp.getApps(context).isEmpty()) {
      FirebaseApp.initializeApp(
        context,
        FirebaseOptions.Builder()
          .setApplicationId("com.aravalli.steelpvcfurniture")
          .setProjectId(PROJECT_ID)
          .setApiKey("fake-api-key-for-emulator")
          .build()
      )
    } else {
      FirebaseApp.getInstance()
    }

    firestore = FirebaseFirestore.getInstance(app, databaseId)
    try {
      firestore.useEmulator(EMULATOR_HOST, FIRESTORE_PORT)
    } catch (_: Exception) {}
    firestore.firestoreSettings = FirebaseFirestoreSettings.Builder()
      .setLocalCacheSettings(MemoryCacheSettings.newBuilder().build())
      .build()

    auth = FirebaseAuth.getInstance(app)
    try {
      auth.useEmulator(EMULATOR_HOST, AUTH_PORT)
    } catch (_: Exception) {}
  }

  @After
  open fun tearDownFirebase() {
    try {
      auth.signOut()
    } catch (_: Exception) {}
  }

  protected suspend fun signInTestUser(email: String): String = withContext(Dispatchers.IO) {
    withTimeout(AUTH_TIMEOUT_MS) {
      val result = try {
        auth.createUserWithEmailAndPassword(email, DEFAULT_PASSWORD).await()
      } catch (_: Exception) {
        auth.signInWithEmailAndPassword(email, DEFAULT_PASSWORD).await()
      }
      checkNotNull(result.user?.uid) { "User auth failed" }
    }
  }

  private companion object {
    const val EMULATOR_HOST = "127.0.0.1"
    const val FIRESTORE_PORT = 8085
    const val AUTH_PORT = 9099
    const val PROJECT_ID = "demo-no-project"
    const val DEFAULT_PASSWORD = "password123"
    const val AUTH_TIMEOUT_MS = 8000L
  }
}
