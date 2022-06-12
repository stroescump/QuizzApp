package com.irinamihaila.quizzapp.ui.newquizz

import androidx.lifecycle.ViewModel
import com.irinamihaila.quizzapp.models.Quiz
import com.irinamihaila.quizzapp.models.UserType
import com.irinamihaila.quizzapp.repo.getAvailableQuizzes
import com.irinamihaila.quizzapp.repo.getQuizDetails
import com.irinamihaila.quizzapp.repo.getQuizzesCreated
import com.irinamihaila.quizzapp.ui.dashboard.QuizCategory
import com.irinamihaila.quizzapp.utils.AppResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class SeeAvailableQuizViewModel(

) : ViewModel() {
    lateinit var quizCategory: QuizCategory
    lateinit var userType: UserType
    val quizzesFlow = MutableStateFlow<AppResult<Quiz>>(AppResult.Progress)

    fun getQuizzesForUser(
        username: String,
        userType: UserType,
    ) =
        when (userType) {
            UserType.AUTHOR -> getQuizzesForAuthor(username)
            UserType.PLAYER -> getQuizzesForPlayer(username)
        }

    private fun getQuizzesForAuthor(username: String) =
        getQuizzesCreated(username, quizCategory) { res ->
            quizzesFlow.update { res }
        }

    private fun getQuizzesForPlayer(username: String) = getAvailableQuizzes(username) { res ->
        when (res) {
            is AppResult.Error -> {
                quizzesFlow.update { AppResult.Error(res.exception) }
            }
            AppResult.Progress -> {
                quizzesFlow.update { AppResult.Progress }
            }
            is AppResult.Success -> {
                res.successData?.onEach { quizDetails ->
                    getQuizDetails(quizDetails.first, Quiz(), quizCategory.name) { quizResult ->
                        when (quizResult) {
                            is AppResult.Error -> {
                                quizzesFlow.update { AppResult.Error(quizResult.exception) }
                            }
                            AppResult.Progress -> {
                                quizzesFlow.update { AppResult.Progress }
                            }
                            is AppResult.Success -> {
                                quizResult.successData?.also { quiz ->
                                    quizzesFlow.update {
                                        AppResult.Success(quiz.apply {
                                            id = quizDetails.first
                                            percentage = quizDetails.second
                                        })
                                    }
                                }
                            }
                        }
                    }
                }
                    ?: quizzesFlow.update { AppResult.Error(Throwable("Unable to find any quizzes. Try creating one first.")) }
            }
        }
    }
}