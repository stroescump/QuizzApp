package com.irinamihaila.quizzapp.models

import kotlinx.parcelize.Parcelize

@Parcelize
data class QuizPlayer(
    override val fullName: String,
    override val username: String,
    override val password: String,
    override val profilePictureURL: String,
) : QuizUser(fullName, username, password, profilePictureURL, "PLAYER")