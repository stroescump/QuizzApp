package com.irinamihaila.quizzapp.models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
enum class UserType : Parcelable {
    AUTHOR, PLAYER
}