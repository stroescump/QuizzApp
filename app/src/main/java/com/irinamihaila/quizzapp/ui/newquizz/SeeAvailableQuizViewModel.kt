package com.irinamihaila.quizzapp.ui.newquizz

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irinamihaila.quizzapp.models.Quiz
import com.irinamihaila.quizzapp.ui.dashboard.QuizCategory
import com.irinamihaila.quizzapp.utils.AppResult
import com.irinamihaila.quizzapp.utils.getAvailableQuizzes
import com.irinamihaila.quizzapp.utils.getQuiz
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class SeeAvailableQuizViewModel(

) : ViewModel() {
    lateinit var quizCategory: QuizCategory
    val availableQuizzesLiveData: MutableLiveData<AppResult<Quiz>> = MutableLiveData()
    val errorLiveData: MutableLiveData<Throwable> = MutableLiveData()

    fun getQuizQuestions() = flow<AppResult<Quiz>> {
        emit(AppResult.Progress)
    }

    fun getQuizzesForUser(username: String) = run {
        availableQuizzesLiveData.postValue(AppResult.Progress)
        viewModelScope.launch(Dispatchers.IO) {
            getAvailableQuizzes(username) {
                when (it) {
                    is AppResult.Error -> {
                        errorLiveData.postValue(it.exception)
                    }
                    AppResult.Progress -> {}
                    is AppResult.Success -> {
                        it.successData?.onEach { quizId ->
//                            val quizList = mutableListOf<Quiz>()
                            getQuiz(quizId, quizCategory.name) { quizResult ->
                                when (quizResult) {
                                    is AppResult.Error -> {
                                        errorLiveData.postValue(quizResult.exception)
                                    }
                                    AppResult.Progress -> {}
                                    is AppResult.Success -> {
                                        quizResult.successData?.also { quiz ->
                                            availableQuizzesLiveData.postValue(
                                                AppResult.Success(
                                                    quiz
                                                )
                                            )
//                                            quiz.takeIf { quizFilter -> quizFilter.category == quizCategory.name }
//                                                ?.let { quizToBeAdded ->
//                                                    quizList.add(quizToBeAdded)
//                                                }
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