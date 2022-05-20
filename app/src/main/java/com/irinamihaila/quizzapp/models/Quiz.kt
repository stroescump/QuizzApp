package com.irinamihaila.quizzapp.models

import com.google.gson.annotations.SerializedName

data class Quiz(
    @SerializedName("id")
    val id: String,
    @SerializedName("questions")
    val questions: Map<String, Pair<String, String>>
)
