package com.irinamihaila.quizzapp.models

import kotlinx.parcelize.Parcelize

@Parcelize
data class QuizAuthor(
    private val firstName: String,
    private val lastName: String,
    private val username: String,
    private val password: String,
    private val profilePictureURL: String,
) : QuizUser(firstName, lastName, username, password, profilePictureURL)
