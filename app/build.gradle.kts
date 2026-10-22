import com.google.gms.googleservices.GoogleServicesPlugin.MissingGoogleServicesStrategy
import java.time.Duration
import java.util.Base64

plugins {
  alias(libs.plugins.android.application)
  alias(libs.plugins.kotlin.compose)
  alias(libs.plugins.google.devtools.ksp)
  alias(libs.plugins.secrets)
  alias(libs.plugins.google.services)
}

// Auto-restore or generate debug.keystore if missing in CI environments
val rootKeystore = rootProject.file("debug.keystore")
val base64Keystore = rootProject.file("debug.keystore.base64")
if (!rootKeystore.exists()) {
  if (base64Keystore.exists()) {
    try {
      val decoded = Base64.getMimeDecoder().decode(base64Keystore.readText().trim())
      rootKeystore.writeBytes(decoded)
    } catch (_: Exception) {}
  }
}
if (!rootKeystore.exists()) {
  try {
    ProcessBuilder(
      "keytool", "-genkey", "-v",
      "-keystore", rootKeystore.absolutePath,
      "-storepass", "android",
      "-alias", "androiddebugkey",
      "-keypass", "android",
      "-keyalg", "RSA",
      "-keysize", "2048",
      "-validity", "10000",
      "-dname", "CN=Android Debug,O=Android,C=US"
    ).redirectErrorStream(true).start().waitFor()
  } catch (_: Exception) {}
}

// Auto-generate fallback google-services.json if missing in CI environments
val gsFile = project.file("google-services.json")
if (!gsFile.exists()) {
  gsFile.writeText(
    """
    {
      "project_info": {
        "project_number": "584333364172",
        "firebase_url": "https://aravallisteel-60c94-default-rtdb.firebaseio.com",
        "project_id": "aravallisteel-60c94",
        "storage_bucket": "aravallisteel-60c94.firebasestorage.app"
      },
      "client": [
        {
          "client_info": {
            "mobilesdk_app_id": "1:584333364172:android:c5ce0f5f6eadebd12d2b69",
            "android_client_info": {
              "package_name": "com.aravalli.steelpvcfurniture"
            }
          },
          "oauth_client": [
            {
              "client_id": "584333364172-7rdinqqt7cjnilj83qj7kka17t2fuj7b.apps.googleusercontent.com",
              "client_type": 3
            }
          ],
          "api_key": [
            {
              "current_key": "AIzaSyAHTQE3sWrw53uAKjHCmxX_JOgvCjNWlDY"
            }
          ],
          "services": {}
        },
        {
          "client_info": {
            "mobilesdk_app_id": "1:584333364172:android:c5ce0f5f6eadebd12d2b69",
            "android_client_info": {
              "package_name": "Com.aravalli.my"
            }
          },
          "oauth_client": [
            {
              "client_id": "584333364172-7rdinqqt7cjnilj83qj7kka17t2fuj7b.apps.googleusercontent.com",
              "client_type": 3
            }
          ],
          "api_key": [
            {
              "current_key": "AIzaSyAHTQE3sWrw53uAKjHCmxX_JOgvCjNWlDY"
            }
          ],
          "services": {}
        }
      ],
      "configuration_version": "1"
    }
    """.trimIndent()
  )
}

android {
  namespace = "com.example"
  compileSdk { version = release(36) { minorApiLevel = 1 } }

  defaultConfig {
    applicationId = "Com.aravalli.my"
    minSdk = 24
    targetSdk = 36
    versionCode = 1
    versionName = "1.0"

    testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
  }

  // Do not remove or modify these signingConfigs. They are necessary for building, installing,
  // and updating Android apps in AI Studio.
  signingConfigs {
    create("release") {
      val keystorePath = System.getenv("KEYSTORE_PATH") ?: "${rootDir}/my-upload-key.jks"
      storeFile = file(keystorePath)
      storePassword = System.getenv("STORE_PASSWORD")
      keyAlias = "upload"
      keyPassword = System.getenv("KEY_PASSWORD")
    }
    create("debugConfig") {
      storeFile = file("${rootDir}/debug.keystore")
      storePassword = "android"
      keyAlias = "androiddebugkey"
      keyPassword = "android"
    }
  }

  buildTypes {
    release {
      isCrunchPngs = false
      isMinifyEnabled = false
      proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
      // Do not remove or modify this signingConfig assignment. It is necessary for Android apps in
      // AI Studio.
      signingConfig = signingConfigs.getByName("release")
    }
    debug {
      // Do not remove or modify this signingConfig assignment. It is necessary for Android apps in
      // AI Studio.
      signingConfig = signingConfigs.getByName("debugConfig")
    }
  }
  compileOptions {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
  }
  buildFeatures {
    compose = true
    buildConfig = true
  }
  testOptions { unitTests { isIncludeAndroidResources = true } }
  dependenciesInfo {
    includeInApk = false
    includeInBundle = true
  }
}

tasks.withType<Test> {
  failFast = true
  timeout.set(Duration.ofMinutes(2))
  testLogging { events("passed", "failed", "skipped") }
}

// Auto-sync GEMINI_API_KEY from system environment or secrets into .env for automatic builds
val envFile = rootProject.file(".env")
val sysGeminiKey = System.getenv("GEMINI_API_KEY") ?: (project.findProperty("GEMINI_API_KEY") as? String)
if (!sysGeminiKey.isNullOrBlank()) {
  if (!envFile.exists()) {
    envFile.writeText("GEMINI_API_KEY=$sysGeminiKey\n")
  } else {
    val content = envFile.readText()
    if (!content.contains("GEMINI_API_KEY=") || content.contains("GEMINI_API_KEY=MY_GEMINI_API_KEY") || content.lines().any { it.trim() == "GEMINI_API_KEY=" }) {
      val filtered = content.lines().filterNot { it.startsWith("GEMINI_API_KEY=") }.joinToString("\n")
      envFile.writeText("$filtered\nGEMINI_API_KEY=$sysGeminiKey\n")
    }
  }
}

// Configure the Secrets Gradle Plugin to use .env and .env.example files
// to match the convention used in Web projects.
secrets {
  propertiesFileName = ".env"
  defaultPropertiesFileName = ".env.example"
  ignoreList.add("FIREBASE_APPCHECK_DEBUG_TOKEN")
}

googleServices { missingGoogleServicesStrategy = MissingGoogleServicesStrategy.WARN }

// Active dependencies for Aravalli Steel PVC Furniture
dependencies {
  implementation(platform(libs.androidx.compose.bom))
  implementation(platform(libs.firebase.bom))
  implementation(libs.androidx.activity.compose)
  implementation(libs.androidx.compose.material.icons.core)
  implementation(libs.androidx.compose.material.icons.extended)
  implementation(libs.androidx.compose.material3)
  implementation(libs.androidx.compose.ui)
  implementation(libs.androidx.compose.ui.graphics)
  implementation(libs.androidx.compose.ui.tooling.preview)
  implementation(libs.androidx.core.ktx)
  implementation(libs.androidx.lifecycle.runtime.compose)
  implementation(libs.androidx.lifecycle.runtime.ktx)
  implementation(libs.androidx.lifecycle.viewmodel.compose)
  implementation(libs.androidx.navigation.compose)
  implementation(libs.androidx.room.ktx)
  implementation(libs.androidx.room.runtime)
  implementation(libs.coil.compose)
  implementation(libs.converter.moshi)
  implementation(libs.firebase.ai)
  implementation(libs.firebase.firestore)
  implementation(libs.firebase.auth)
  implementation(libs.androidx.credentials)
  implementation(libs.androidx.credentials.play.services)
  implementation(libs.googleid)
  implementation(libs.firebase.appcheck.recaptcha)
  implementation(libs.firebase.appcheck.debug)
  implementation(libs.kotlinx.coroutines.android)
  implementation(libs.kotlinx.coroutines.core)
  implementation(libs.logging.interceptor)
  implementation(libs.moshi.kotlin)
  implementation(libs.okhttp)
  implementation(libs.retrofit)
  testImplementation(libs.androidx.compose.ui.test.junit4)
  testImplementation(libs.androidx.core)
  testImplementation(libs.androidx.junit)
  testImplementation(libs.junit)
  testImplementation(libs.kotlinx.coroutines.test)
  testImplementation(libs.robolectric)
  androidTestImplementation(platform(libs.androidx.compose.bom))
  androidTestImplementation(libs.androidx.compose.ui.test.junit4)
  androidTestImplementation(libs.androidx.espresso.core)
  androidTestImplementation(libs.androidx.junit)
  androidTestImplementation(libs.androidx.runner)
  debugImplementation(libs.androidx.compose.ui.test.manifest)
  debugImplementation(libs.androidx.compose.ui.tooling)
  "ksp"(libs.androidx.room.compiler)
  "ksp"(libs.moshi.kotlin.codegen)
}
