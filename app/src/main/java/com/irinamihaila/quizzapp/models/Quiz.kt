package com.irinamihaila.quizzapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Quiz(
    var id: String? = "",
    var name: String? = "",
    var category: String? = "",
    var issuedDate: String? = "",
    var percentage: Int? = null,
    var isRedo: Boolean? = false,
    var questions: List<Question?>? = emptyList()
) : Parcelable

@Parcelize
data class Question(
    val question: String? = "",
    val answer1: String? = "",
    val answer2: String? = "",
    val answer3: String? = "",
    val answer4: String? = "",
    val correctAnswer: String? = ""
) : Parcelable
