package com.irinamihaila.quizzapp.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
open class QuizUser(
    @SerializedName("firstName")
    private val firstName: String,
    @SerializedName("lastName")
    private val lastName: String,
    @SerializedName("username")
    private val username: String,
    @SerializedName("password")
    private val password: String,
    @SerializedName("profilePictureURL")
    private val profilePictureURL: String,
) : Parcelable
