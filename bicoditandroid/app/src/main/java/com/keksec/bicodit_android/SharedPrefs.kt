package com.keksec.bicodit_android

import android.content.Context

/**
 * This class holds shared preferences and performs relating operations
 */
class SharedPrefs(context: Context) {
    val PREFS_FILENAME = "prefs"
    val prefs = context.getSharedPreferences(PREFS_FILENAME, 0)
    val AUTH_TOKEN = "authToken"
    val USER_ID = "userId"

    var accessToken: String?
        get() = prefs.getString(AUTH_TOKEN, "")
        set(value) = prefs.edit().putString(AUTH_TOKEN, value).apply()

    var userId: String?
        get() = prefs.getString(USER_ID, "")
        set(value) = prefs.edit().putString(USER_ID, value).apply()
}

