package com.keksec.bicodit_android

import android.content.Context

/**
 * This class performs operations with shared preferences
 */
class SharedPrefs(context: Context) {
    val PREFS_FILENAME = "prefs"
    val prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
    val AUTH_TOKEN = "authToken"

    var accessToken: String?
        get() = prefs.getString(AUTH_TOKEN, "")
        set(value) = prefs.edit().putString(AUTH_TOKEN, value).apply()
}