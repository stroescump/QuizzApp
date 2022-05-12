package com.irinamihaila.quizzapp.models

import kotlinx.parcelize.Parcelize

@Parcelize
data class QuizPlayer(
    private val firstName: String,
    private val lastName: String,
    private val username: String,
    private val password: String,
    private val profilePictureURL: String,
) : QuizUser(firstName, lastName, username, password, profilePictureURL)