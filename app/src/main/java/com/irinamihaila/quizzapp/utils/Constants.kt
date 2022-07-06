package com.irinamihaila.quizzapp.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

object Constants {
    const val IS_EDIT = "IS_EDIT"
    const val IS_NEW_QUIZ = "IS_NEW_QUIZ"
    const val QUIZ_CATEGORY = "QUIZ_CATEGORY"
    const val QUIZ_ID = "QUIZ_ID"
    const val QUIZ = "QUIZ"
    const val USER_TYPE = "USER_TYPE"
    @SuppressLint("ConstantLocale")
    val dateFormatter = SimpleDateFormat("dd/MM/yy", Locale.getDefault())
}