package com.irinamihaila.quizzapp.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.irinamihaila.quizzapp.models.QuizUser
import com.irinamihaila.quizzapp.ui.newquizz.createUserNode
import com.irinamihaila.quizzapp.ui.newquizz.getUserNode
import com.irinamihaila.quizzapp.utils.AppResult
import com.irinamihaila.quizzapp.utils.SharedPrefsUtils

class AuthenticationViewModel(val sharedPrefsUtils: SharedPrefsUtils) : ViewModel() {
    val uiStateLiveData = MutableLiveData<AppResult<String>>()

    fun login(username: String, password: String) = run {
        uiStateLiveData.value = AppResult.Progress
        getUserNode(username).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists() && snapshot.hasChildren()) {
                    val isValidCredentials = snapshot.child("password").value == password
                    if (isValidCredentials.not()) {
                        uiStateLiveData.postValue(
                            AppResult.Error(
                                Exception("Credentials incorrect. Please re-check.")
                            )
                        )
                    } else uiStateLiveData.postValue(AppResult.Success("Login successful."))
                } else {
                    uiStateLiveData.postValue(AppResult.Error(Exception("Account does not exist.")))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                uiStateLiveData.postValue(AppResult.Error(error.toException()))
            }

        })
    }

    fun register(quizUser: QuizUser) = run {
        uiStateLiveData.value = AppResult.Progress
        createUserNode(quizUser)
        Firebase.database.getReference("users")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.hasChild(quizUser.username)) {
                        uiStateLiveData.postValue(AppResult.Success(null))
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    uiStateLiveData.postValue(AppResult.Error(error.toException()))
                }
            })
    }
}