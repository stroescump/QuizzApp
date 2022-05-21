package com.irinamihaila.quizzapp.models

import com.google.gson.annotations.SerializedName

data class Quiz(
    @SerializedName("id")
    val id: String,
    @SerializedName("questions")
    val questions: List<Question>
)

data class Question(
    val question: String,
    val answer1: String,
    val answer2: String,
    val answer3: String,
    val answer4: String,
    val correctAnswer: String
)
