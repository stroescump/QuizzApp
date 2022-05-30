package com.irinamihaila.quizzapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
open class QuizUser(
    @SerializedName("fullName")
    open val fullName: String,
    @SerializedName("username")
    open val username: String,
    @SerializedName("password")
    open val password: String,
    @SerializedName("profilePictureURL")
    open val profilePictureURL: String,
    @SerializedName("userType")
    open val userType: String,
    @SerializedName("quizId")
    val quizId: List<String> = emptyList(),
) : Parcelable
