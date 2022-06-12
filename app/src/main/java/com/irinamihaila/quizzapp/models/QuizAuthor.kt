package com.irinamihaila.quizzapp.models

import com.irinamihaila.quizzapp.models.UserType.AUTHOR
import kotlinx.parcelize.Parcelize

@Parcelize
data class QuizAuthor(
    override val fullName: String,
    override val username: String,
    override val password: String,
    override val profilePictureURL: String,
) : QuizUser(fullName, username, password, profilePictureURL, AUTHOR.name)
