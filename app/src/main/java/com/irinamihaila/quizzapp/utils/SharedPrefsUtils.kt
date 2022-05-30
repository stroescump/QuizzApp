package com.irinamihaila.quizzapp.utils

import android.content.Context
import com.irinamihaila.quizzapp.R

class SharedPrefsUtils(private val ctx: Context) {
    companion object {
        const val USER_KEY = "USER_KEY"
    }

    fun getUserId() = run {
        val prefs = ctx.getSharedPreferences(
            ctx.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        prefs.getString(USER_KEY, null)
    }

    fun saveUserId(userId: String) = run {
        val prefs = ctx.getSharedPreferences(
            ctx.getString(R.string.preference_file_key),
            Context.MODE_PRIVATE
        )
        prefs.edit().apply {
            putString(USER_KEY, userId)
            apply()
        }
    }
}