package com.example.util

import android.content.Context
import android.content.SharedPreferences

/**
 * Manages local session persistence for email/phone login and signup
 * so that users stay logged in across app restarts.
 */
object UserSessionManager {
  private const val PREF_NAME = "aravalli_session"
  private const val KEY_IS_LOGGED_IN = "is_logged_in"
  private const val KEY_USER_NAME = "user_name"
  private const val KEY_USER_EMAIL = "user_email"
  private const val KEY_USER_PHONE = "user_phone"
  private const val KEY_USER_ROLE = "user_role"

  private fun getPrefs(context: Context): SharedPreferences {
    return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
  }

  fun saveSession(context: Context, name: String, emailOrPhone: String, role: String = "ADMIN") {
    getPrefs(context).edit().apply {
      putBoolean(KEY_IS_LOGGED_IN, true)
      putString(KEY_USER_NAME, name.ifBlank { "Aravalli Partner" })
      if (emailOrPhone.contains("@")) {
        putString(KEY_USER_EMAIL, emailOrPhone)
        putString(KEY_USER_PHONE, "")
      } else {
        putString(KEY_USER_PHONE, emailOrPhone)
        putString(KEY_USER_EMAIL, "")
      }
      putString(KEY_USER_ROLE, role)
      apply()
    }
  }

  fun isLoggedIn(context: Context): Boolean {
    return getPrefs(context).getBoolean(KEY_IS_LOGGED_IN, false)
  }

  fun getUserName(context: Context): String {
    return getPrefs(context).getString(KEY_USER_NAME, "Aravalli Partner") ?: "Aravalli Partner"
  }

  fun getUserEmailOrPhone(context: Context): String {
    val email = getPrefs(context).getString(KEY_USER_EMAIL, "") ?: ""
    if (email.isNotBlank()) return email
    return getPrefs(context).getString(KEY_USER_PHONE, "") ?: ""
  }

  fun getUserRole(context: Context): String {
    return getPrefs(context).getString(KEY_USER_ROLE, "ADMIN") ?: "ADMIN"
  }

  fun clearSession(context: Context) {
    getPrefs(context).edit().clear().apply()
  }
}
