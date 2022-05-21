package com.irinamihaila.quizzapp.ui.newquizz

import androidx.lifecycle.ViewModel
import com.irinamihaila.quizzapp.models.Quiz
import com.irinamihaila.quizzapp.utils.AppResult
import kotlinx.coroutines.flow.flow

class TakeQuizViewModel(

) : ViewModel() {

    fun getQuizQuestions() = flow<AppResult<Quiz>> {
        emit(AppResult.Progress)
    }
}