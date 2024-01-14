package com.saeedtechies.commons.utils

import android.content.Context
import com.saeedtechies.commons.common.getSharedPrefs

class Config(context: Context) {
    private val prefs = context.getSharedPrefs()

    companion object {
        fun newInstance(context: Context) = Config(context)
    }

    var userId: String
        get() = prefs.getString(ID, "")!!
        set(userId) = prefs.edit().putString(ID, userId).apply()

    var userOf: Int
        get() = prefs.getInt(USER_OF, 0)
        set(userId) = prefs.edit().putInt(USER_OF, userId).apply()

    var currentUserId: String
        get() = prefs.getString(USER_ID, "")!!
        set(userId) = prefs.edit().putString(USER_ID, userId).apply()

    var userRole: String
        get() = prefs.getString(USER_ROLE, "")!!
        set(userRole) = prefs.edit().putString(USER_ROLE, userRole).apply()

    var userName: String
        get() = prefs.getString(USER_NAME, "")!!
        set(userRole) = prefs.edit().putString(USER_NAME, userRole).apply()

    var userEmail: String
        get() = prefs.getString(USER_EMAIL, "")!!
        set(userRole) = prefs.edit().putString(USER_EMAIL, userRole).apply()

    var userPassword: String
        get() = prefs.getString(USER_PASSWORD, "")!!
        set(string) = prefs.edit().putString(USER_PASSWORD, string).apply()

    var isLoggedIn: Boolean
        get() = prefs.getBoolean(IS_LOGGED_IN, false)
        set(isLoggedIn) = prefs.edit().putBoolean(IS_LOGGED_IN, isLoggedIn).apply()

    var apiToken: String
        get() = prefs.getString(API_TOKEN, "")!!
        set(userId) = prefs.edit().putString(API_TOKEN, userId).apply()

    var isNewOrderCreated: Boolean
        get() = prefs.getBoolean(IS_NEW_ORDER_CREATED, false)
        set(isLoggedIn) = prefs.edit().putBoolean(IS_NEW_ORDER_CREATED, isLoggedIn).apply()

    var isAreaCleared: Boolean
        get() = prefs.getBoolean(IS_AREA_CLEARED, false)
        set(isAreaCleared) = prefs.edit().putBoolean(IS_AREA_CLEARED, isAreaCleared).apply()

    var isReloadData: Boolean
        get() = prefs.getBoolean(IS_RELOAD_DATA, false)
        set(isAreaCleared) = prefs.edit().putBoolean(IS_RELOAD_DATA, isAreaCleared).apply()

    var noOfTimesPermissionAsked: Int
        get() = prefs.getInt(NO_OF_TIMES_PERMISSION_ASKED, 0)
        set(userId) = prefs.edit().putInt(NO_OF_TIMES_PERMISSION_ASKED, userId).apply()

    fun clearAll() {
        prefs.edit().clear().apply()
    }
}
