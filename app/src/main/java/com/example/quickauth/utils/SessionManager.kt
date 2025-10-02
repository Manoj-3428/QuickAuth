package com.example.quickauth.utils

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    
    companion object {
        private const val PREFS_NAME = "QuickAuthSession"
        private const val KEY_IS_LOGGED_IN = "is_logged_in"
        private const val KEY_USER_ID = "user_id"
        private const val KEY_USER_NAME = "user_name"
        private const val KEY_USER_EMAIL = "user_email"
        private const val KEY_USER_PHONE = "user_phone"
    }
    
    fun saveUserSession(
        userId: String,
        userName: String,
        userEmail: String,
        userPhone: String
    ) {
        prefs.edit().apply {
            putBoolean(KEY_IS_LOGGED_IN, true)
            putString(KEY_USER_ID, userId)
            putString(KEY_USER_NAME, userName)
            putString(KEY_USER_EMAIL, userEmail)
            putString(KEY_USER_PHONE, userPhone)
            apply()
        }
    }
    
    fun isLoggedIn(): Boolean {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }
    
    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }
    
    fun getUserName(): String? {
        return prefs.getString(KEY_USER_NAME, null)
    }
    
    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }
    
    fun getUserPhone(): String? {
        return prefs.getString(KEY_USER_PHONE, null)
    }
    
    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
