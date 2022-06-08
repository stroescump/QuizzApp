package com.irinamihaila.quizzapp.ui.newquizz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.GenericTypeIndicator
import com.irinamihaila.quizzapp.models.Question
import com.irinamihaila.quizzapp.ui.dashboard.QuizCategory
import com.irinamihaila.quizzapp.utils.SharedPrefsUtils
import com.irinamihaila.quizzapp.utils.createNewQuiz
import com.irinamihaila.quizzapp.utils.getQuizFromDB
import com.irinamihaila.quizzapp.utils.getQuizzesFromUsername
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class QuizViewModel(private val sharedPrefs: SharedPrefsUtils) : ViewModel() {

    val uiState = MutableStateFlow<Pair<Boolean, String?>?>(null)
    val currentQuizId = MutableStateFlow<String?>(null)

    fun uploadQuestion(question: Question, quizId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val quiz = sharedPrefs.getUsername()?.let { getQuizFromDB(quizId) }
            quiz?.get()?.addOnSuccessListener {
                if (it.exists()) {
                    val generic = object : GenericTypeIndicator<MutableList<Question>>() {}
                    var list: MutableList<Question>
                    it.child("questions").apply {
                        list = getValue(generic) ?: mutableListOf()
                        list.add(question)
                    }
                    it.child("questions").ref.setValue(list)
                } else {
                    quiz.setValue(listOf(question))
                    uiState.update { true to null }
                }
            }?.addOnFailureListener { exception ->
                uiState.update { false to exception.localizedMessage }
            }
        }
    }

    fun createQuiz(quizCategory: QuizCategory) {
        viewModelScope.launch {

            val quizDB = createNewQuiz().apply {
                child("category").setValue(quizCategory.name)
            }
            sharedPrefs.getUsername()?.let {
                getQuizzesFromUsername(it).get()
                    .addOnSuccessListener { res ->
                        addNewQuiz(res, quizDB.key!!)
                        currentQuizId.update { quizDB.key }
                        uiState.update { true to null }
                    }
                    .addOnFailureListener { exception ->
                        uiState.update { false to exception.localizedMessage }
                    }
            }
        }
    }

    private fun addNewQuiz(
        it: DataSnapshot,
        path: String
    ) {
        it.ref.child(path).setValue("")
    }
}

