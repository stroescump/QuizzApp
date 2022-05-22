package com.irinamihaila.quizzapp.models

import kotlinx.parcelize.Parcelize

@Parcelize
data class QuizPlayer(
    override val firstName: String,
    override val lastName: String,
    override val username: String,
    override val password: String,
    override val profilePictureURL: String,
) : QuizUser(firstName, lastName, username, password, profilePictureURL)