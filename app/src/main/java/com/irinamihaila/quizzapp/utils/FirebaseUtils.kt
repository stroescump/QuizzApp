package com.irinamihaila.quizzapp.utils

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.irinamihaila.quizzapp.models.Quiz
import com.irinamihaila.quizzapp.models.QuizUser

fun getUserNode(username: String) = Firebase.database.getReference("users/$username")

fun getLeaderboardFirebase(
    quizId: String,
    handler: (leaderboard: AppResult<List<Pair<String, Int>>>) -> Unit
) =
    Firebase.database.getReference("users").addValueEventListener(object : ValueEventListener {
        override fun onDataChange(snapshot: DataSnapshot) {
            if (snapshot.exists() && snapshot.hasChildren()) {
                val leaderboard = mutableListOf<Pair<String, Int>>()
                snapshot.children.onEach { user ->
                    val fullName = getFullname(user)
                    if (isSubscribedToQuiz(user)) {
                        val quiz = user.child("availableQuizzes").child(quizId)
                        val percentage = getPercentage(quiz)
                        percentage?.let { leaderboard.add(fullName to it) }
                    }
                }
                if (leaderboard.isNotEmpty()) {
                    handler(AppResult.Success(leaderboard))
                } else {
                    handler(AppResult.Error(Throwable("No user is subscribed to this quiz.")))
                }
            }
        }

        private fun isSubscribedToQuiz(user: DataSnapshot) =
            user.hasChild("availableQuizzes") && user.child("availableQuizzes").hasChild(quizId)

        private fun getPercentage(quiz: DataSnapshot) =
            if (quiz.hasChild("percentage")) {
                quiz.child("percentage").getValue(Int::class.java)
            } else null

        private fun getFullname(user: DataSnapshot) =
            (user.child("fullName").getValue(String::class.java)
                ?: throw IllegalArgumentException("User must have fullName."))

        override fun onCancelled(error: DatabaseError) {
            handler(AppResult.Error(error.toException()))
        }
    })

fun getQuizNode(username: String) = Firebase.database.getReference("users/$username/quiz")
fun getLastQuizNode(username: String, handler: (key: String?) -> Unit) =
    Firebase.database.getReference("users/$username/quiz")
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

fun getQuiz(id: String, category: String, handler: (quiz: AppResult<Quiz>) -> Unit) =
    Firebase.database.getReference("quizzes").get().addOnSuccessListener { snapshot ->
        fun throwNoElementException() {
            handler(AppResult.Error(NoSuchElementException("Unable to find any quizzes. Try creating one first.")))
        }

        try {
            if (snapshot.hasChildren()) {
                snapshot.children.filter {
                    matchById(it, id) && matchByCategory(it, category)
                }.also { quiz ->
                    if (quiz.isNotEmpty()) {
                        handler(AppResult.Success(quiz.first().getValue(Quiz::class.java)))
                    } else {
                        throwNoElementException()
                    }
                }
            } else {
                throwNoElementException()
            }
        } catch (e: NoSuchElementException) {
            throwNoElementException()
        }
    }.addOnFailureListener { handler(AppResult.Error(it)) }


private fun matchByCategory(quiz: DataSnapshot?, category: String) = run {
    quiz?.let {
        return (quiz.exists() && quiz.hasChild("category") && quiz.child("category")
            .getValue(String::class.java) == category)
    }
    return@run false
}


private fun matchById(it: DataSnapshot, id: String) =
    it.child("id").getValue(String::class.java) == id


fun getAvailableQuizzes(
    username: String,
    handler: (quiz: AppResult<List<String>>) -> Unit
) =
    getUserNode(username).child("availableQuizzes")
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()) {
                    val availableQuizList = mutableListOf<String>()
                    snapshot.children.forEach {
                        it.key?.let { value -> availableQuizList.add(value) }
                    }
                    handler(AppResult.Success(availableQuizList))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                handler(AppResult.Error(error.toException()))
            }
        })