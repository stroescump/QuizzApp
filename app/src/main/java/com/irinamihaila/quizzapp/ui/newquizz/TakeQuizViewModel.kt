package com.irinamihaila.quizzapp.ui.newquizz

import androidx.lifecycle.ViewModel
import com.irinamihaila.quizzapp.models.Quiz
import kotlinx.coroutines.flow.MutableStateFlow

class TakeQuizViewModel : ViewModel() {
    lateinit var quiz: Quiz

    val currentQuestion: MutableStateFlow<Int> = MutableStateFlow(0)

    init {
    }
}