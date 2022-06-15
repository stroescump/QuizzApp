package com.irinamihaila.quizzapp.ui.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.irinamihaila.quizzapp.models.Quiz
import com.irinamihaila.quizzapp.repo.getFeedbackForQuiz
import com.irinamihaila.quizzapp.repo.sendFeedbackFirebase
import com.irinamihaila.quizzapp.utils.AppResult
import com.irinamihaila.quizzapp.utils.SharedPrefsUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class FeedbackViewModel(private val sharedPrefs: SharedPrefsUtils) : ViewModel() {
    val uiState = MutableStateFlow<AppResult<Quiz>?>(null)

    fun retrieveFeedback(quiz: Quiz) {
        getFeedbackForQuiz(quiz) { res ->
            uiState.update { res }
        }
    }

    fun sendFeedback(feedback: String, quiz: Quiz) {
        sendFeedbackFirebase(feedback, quiz) { res ->
            uiState.update { res }
        }
    }

    class Factory(private val sharedPrefs: SharedPrefsUtils) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FeedbackViewModel(sharedPrefs) as T
        }
    }
}