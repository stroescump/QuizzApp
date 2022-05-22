package com.irinamihaila.quizzapp.ui.newquizz

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.irinamihaila.quizzapp.models.Question
import com.irinamihaila.quizzapp.models.QuizUser
import com.irinamihaila.quizzapp.utils.SharedPrefsUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class QuizViewModel(private val sharedPrefs: SharedPrefsUtils) : ViewModel() {

    fun uploadQuestion(question: Question, quizId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            //TODO Replace with SharedPrefs & Add handlers for failure
            val quiz = getQuizNode("marusanki").child(quizId)
            quiz.get().addOnSuccessListener {
                if (it.exists()) {
                    val generic = object : GenericTypeIndicator<MutableList<Question>>() {}
                    val list = it.getValue(generic)
                    list?.add(question)
                    quiz.setValue(list)
                } else {
                    quiz.setValue(listOf(question))
                }
            }
        }
    }

    fun createQuiz() {
        viewModelScope.launch {
            //TODO Replace with SharedPrefs & Add handlers for failure
            val quiz = getQuizNode("marusanki")
            quiz.get().addOnSuccessListener {
                if (it.exists()) {
                    quiz.child("${it.childrenCount}").setValue("")
                } else {
                    quiz.setValue(listOf(""))
                }
            }
        }
    }
}

fun getUserNode(userId: String) = Firebase.database.getReference("users/$userId")
fun getQuizNode(userId: String) = Firebase.database.getReference("users/$userId/quiz")
fun getLastQuizNode(userId: String, handler: (key: String?) -> Unit) =
    Firebase.database.getReference("users/$userId/quiz")
        .get()
        .addOnSuccessListener {
            try {
                if (it.children.count() > 0) {
                    handler(it.children.last().ref.key)
                } else {
                    handler(null)
                }
            } catch (e: Throwable) {
                handler(null)
            }
        }

fun createUserNode(quizUser: QuizUser) =
    Firebase.database.getReference("users/${quizUser.username}").setValue(quizUser)

