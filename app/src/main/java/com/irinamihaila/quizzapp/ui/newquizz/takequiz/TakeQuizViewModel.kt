package com.irinamihaila.quizzapp.ui.newquizz.takequiz

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.irinamihaila.quizzapp.models.Quiz
import com.irinamihaila.quizzapp.repo.getLeaderboardFirebase
import com.irinamihaila.quizzapp.repo.getUserNode
import com.irinamihaila.quizzapp.utils.AppResult
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TakeQuizViewModel : ViewModel() {
    lateinit var quiz: Quiz

    val currentQuestion: MutableStateFlow<Int> = MutableStateFlow(0)
    val currentTime: MutableStateFlow<Int> = MutableStateFlow(0)
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

    private var _timerStateFlow = MutableStateFlow(0)
    val timerStateFlow: StateFlow<Int> = _timerStateFlow

    private var job: Job? = null

    fun toggleTime(totalSeconds: Int) {
        job = if (job == null) {
            viewModelScope.launch {
                initTimer(totalSeconds)
                    .onCompletion { _timerStateFlow.emit(-1) }
                    .collect { _timerStateFlow.emit(it) }
            }
        } else {
            job?.cancel()
            null
        }
    }

    /**
     * The timer emits the total seconds immediately.
     * Each second after that, it will emit the next value.
     */

    private fun initTimer(totalSeconds: Int): Flow<Int> =
        (totalSeconds - 1 downTo 0).asFlow() // Emit total - 1 because the first was emitted onStart
            .onEach { delay(1000) } // Each second later emit a number
            .onStart { emit(totalSeconds) } // Emit total seconds immediately
            .conflate() // In case the operation onTick takes some time, conflate keeps the time ticking separately
            .transform { remainingSeconds: Int ->
                emit(remainingSeconds)
            }
}