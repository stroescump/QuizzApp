package com.irinamihaila.quizzapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
open class QuizUser(
    @SerializedName("firstName")
    open val firstName: String,
    @SerializedName("lastName")
    open val lastName: String,
    @SerializedName("username")
    open val username: String,
    @SerializedName("password")
    open val password: String,
    @SerializedName("profilePictureURL")
    open val profilePictureURL: String,
    @SerializedName("quizId")
    val quizId: List<String> = emptyList(),
) : Parcelable
