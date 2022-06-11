package com.irinamihaila.quizzapp.ui.newquizz

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.GenericTypeIndicator
import com.irinamihaila.quizzapp.models.Question
import com.irinamihaila.quizzapp.models.Quiz
import com.irinamihaila.quizzapp.repo.createNewQuiz
import com.irinamihaila.quizzapp.repo.getQuizFromDB
import com.irinamihaila.quizzapp.repo.getQuizzesFromUsername
import com.irinamihaila.quizzapp.ui.dashboard.QuizCategory
import com.irinamihaila.quizzapp.utils.AppResult
import com.irinamihaila.quizzapp.utils.SharedPrefsUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

class QuizViewModel(private val sharedPrefs: SharedPrefsUtils) : ViewModel() {

    val uiState = MutableStateFlow<Pair<Boolean, String?>?>(null)
    val currentQuizId = MutableStateFlow<String?>(null)
    val editQuizLiveData = MutableLiveData<AppResult<Quiz>>()

    fun uploadQuestion(question: Question, quizId: String) {
        val quiz = sharedPrefs.getUsername()?.let { getQuizFromDB(quizId) }
        quiz?.get()?.addOnSuccessListener {
            if (it.exists()) {
                val generic = object : GenericTypeIndicator<MutableList<Question>>() {}
                var list: MutableList<Question>
                it.child("questions").apply {
                    list = getValue(generic) ?: mutableListOf()
                    list.add(question.apply { questionId = list.lastIndex.inc().toString() })
                }
                it.child("questions").ref.setValue(list)
            } else {
                quiz.child("questions").ref.setValue(listOf(question))
                uiState.update { true to null }
            }
        }?.addOnFailureListener { exception ->
            uiState.update { false to exception.localizedMessage }
        }
    }

    fun createQuiz(quizCategory: QuizCategory) {
        val quizDB = createNewQuiz().apply {
            child("category").setValue(quizCategory.name)
        }
        sharedPrefs.getUsername()?.let {
            getQuizzesFromUsername(it).get()
                .addOnSuccessListener { res ->
                    addNewQuiz(res, quizDB.key!!, quizCategory.name)
                    currentQuizId.update { quizDB.key }
                    uiState.update { true to null }
                }
                .addOnFailureListener { exception ->
                    uiState.update { false to exception.localizedMessage }
                }
        }
    }

    private fun addNewQuiz(
        usernameQuizzes: DataSnapshot,
        quizKey: String,
        category: String
    ) {
        usernameQuizzes.ref.child(quizKey).setValue(Quiz(id = quizKey))
            .addOnCompleteListener {
                usernameQuizzes.ref.child(quizKey).child("category").setValue(category)
            }
    }

    fun getQuestionsFromSelectedQuiz(quizId: String) {
        editQuizLiveData.postValue(AppResult.Progress)
        getQuizFromDB(quizId).get()
            .addOnSuccessListener {
                val quiz = it.getValue(Quiz::class.java)
                editQuizLiveData.postValue(AppResult.Success(quiz))
            }.addOnFailureListener {
                editQuizLiveData.postValue(AppResult.Error(it))
            }
    }

    fun updateQuestion(questionToBeUpdated: Question, newQuestion: Question, quizId: String) {
        sharedPrefs.getUsername()?.let {
            questionToBeUpdated.questionId?.let { id ->
                getQuizFromDB(quizId).child("questions").child(id)
                    .setValue(newQuestion.apply { questionId = questionToBeUpdated.questionId })
                    .addOnSuccessListener {
                        uiState.update { true to null }
                    }
                    .addOnFailureListener { exception ->
                        uiState.update { false to exception.localizedMessage }
                    }
            }
        }
    }

}