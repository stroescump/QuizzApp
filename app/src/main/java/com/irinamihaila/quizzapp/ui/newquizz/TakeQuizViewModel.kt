package com.irinamihaila.quizzapp.ui.newquizz

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.irinamihaila.quizzapp.models.Quiz
import com.irinamihaila.quizzapp.utils.AppResult
import com.irinamihaila.quizzapp.utils.getLeaderboardFirebase
import com.irinamihaila.quizzapp.utils.getUserNode
import kotlinx.coroutines.flow.MutableStateFlow

class TakeQuizViewModel : ViewModel() {
    lateinit var quiz: Quiz

    val currentQuestion: MutableStateFlow<Int> = MutableStateFlow(0)
    var correctAnswers: Int = 0

    val leaderboardLiveData = MutableLiveData<List<Pair<String, Int>>>()
    val errorLiveData = MutableLiveData<Throwable>()

    fun getLeaderboard(quizId: String) {
        getLeaderboardFirebase(quizId) { leaderboard ->
            when (leaderboard) {
                is AppResult.Error -> errorLiveData.postValue(leaderboard.exception)
                AppResult.Progress -> {}
                is AppResult.Success -> {
                    leaderboard.successData?.let { leaderboardLiveData.postValue(it) }
                        ?: errorLiveData.postValue(
                            Throwable("An error occured while fetching leaderboard. Please retry.")
                        )
                }
            }
        }
    }

    fun sendResults(username: String, quizId: String) {
        getUserNode(username).child("availableQuizzes").child(quizId).child("percentage")
            .setValue(quiz.questions?.size?.toFloat()
                ?.let { correctAnswers.toFloat().div(it).times(100f).toInt() })
    }
}