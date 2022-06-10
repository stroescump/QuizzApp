package com.irinamihaila.quizzapp.ui.newquizz

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irinamihaila.quizzapp.models.Quiz
import com.irinamihaila.quizzapp.ui.dashboard.QuizCategory
import com.irinamihaila.quizzapp.utils.AppResult
import com.irinamihaila.quizzapp.repo.getAvailableQuizzes
import com.irinamihaila.quizzapp.repo.getQuiz
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SeeAvailableQuizViewModel(

) : ViewModel() {
    lateinit var quizCategory: QuizCategory
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()
    val quizzesFlow = MutableStateFlow<AppResult<Quiz>>(AppResult.Progress)

    fun getQuizzesForUser(username: String) = run {
        viewModelScope.launch(Dispatchers.IO) {
            getAvailableQuizzes(username) {
                when (it) {
                    is AppResult.Error -> {
                        errorLiveData.postValue(it.exception)
                    }
                    AppResult.Progress -> {}
                    is AppResult.Success -> {
                        it.successData?.onEach { quizDetails ->
                            getQuiz(quizDetails.first, quizCategory.name) { quizResult ->
                                when (quizResult) {
                                    is AppResult.Error -> {
                                        errorLiveData.postValue(quizResult.exception)
                                    }
                                    AppResult.Progress -> {}
                                    is AppResult.Success -> {
                                        quizResult.successData?.also { quiz ->
                                            quizzesFlow.update {
                                                AppResult.Success(
                                                    quiz.apply { percentage = quizDetails.second }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        } ?: errorLiveData.postValue(Throwable("An error occured. Please retry."))
                    }
                }
            }
        }
    }
}