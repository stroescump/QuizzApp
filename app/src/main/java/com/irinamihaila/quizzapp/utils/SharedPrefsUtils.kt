package com.irinamihaila.quizzapp.utils

import android.content.Context
import com.irinamihaila.quizzapp.R

class SharedPrefsUtils(private val ctx: Context) {
    companion object {
        const val USER_KEY = "USER_KEY"
        const val FULL_NAME_KEY = "FULL_NAME"
        const val USER_TYPE_KEY = "USER_TYPE"
    }

    fun getUsername() = run {
        val prefs = ctx.getSharedPreferences(
            ctx.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        prefs.getString(USER_KEY, null)
    }

    fun saveUsername(username: String) = run {
        val prefs = ctx.getSharedPreferences(
            ctx.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        prefs.edit().apply {
            putString(USER_KEY, username)
            apply()
        }
    }

    fun getFullName() = run {
        val prefs = ctx.getSharedPreferences(
            ctx.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        prefs.getString(FULL_NAME_KEY, null)
    }

    fun saveFullName(fullName: String) = run {
        val prefs = ctx.getSharedPreferences(
            ctx.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        prefs.edit().apply {
            putString(FULL_NAME_KEY, fullName)
            apply()
        }
    }

    fun getUserType(): String? = run {
        val prefs = ctx.getSharedPreferences(
            ctx.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        prefs.getString(USER_TYPE_KEY, null)
    }

    fun saveUserType(userType: String) {
        val prefs = ctx.getSharedPreferences(
            ctx.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        prefs.edit().apply {
            putString(USER_TYPE_KEY, userType)
            apply()
        }
    }
}