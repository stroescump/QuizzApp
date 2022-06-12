package com.irinamihaila.quizzapp.repo

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.irinamihaila.quizzapp.models.Quiz
import com.irinamihaila.quizzapp.models.QuizUser
import com.irinamihaila.quizzapp.ui.dashboard.QuizCategory
import com.irinamihaila.quizzapp.utils.AppResult

fun getUserNode(username: String) = Firebase.database.getReference("users/$username")

fun deleteQuizFromUsers(quizId: String, handler: (AppResult<Nothing>) -> Unit) =
    Firebase.database.getReference("users").get().addOnSuccessListener {
        it.children.onEach { user ->
            val availableQuizzes = user.child("availableQuizzes")
            if (availableQuizzes.exists() && availableQuizzes.hasChildren()) {
                availableQuizzes.children.firstOrNull { quiz -> quiz.key == quizId }
                    ?.ref?.removeValue()
                    ?.addOnSuccessListener {
                        getQuizFromDB(quizId).removeValue()
                            .addOnSuccessListener {
                                handler(AppResult.Success(null))
                            }
                            .addOnFailureListener { e ->
                                handler(AppResult.Error(e))
                            }

                    }?.addOnFailureListener { e ->
                        handler(AppResult.Error(e))
                    }
            }
        }
    }.addOnFailureListener { e ->
        handler(AppResult.Error(e))
    }

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

        private fun getFullname(user: DataSnapshot) =
            (user.child("fullName").getValue(String::class.java)
                ?: throw IllegalArgumentException("User must have fullName."))

        override fun onCancelled(error: DatabaseError) {
            handler(AppResult.Error(error.toException()))
        }
    })

private fun getPercentage(quiz: DataSnapshot) =
    if (quiz.hasChild("percentage")) {
        quiz.child("percentage").getValue(Int::class.java)
    } else null

fun getQuizzesFromUsername(username: String) =
    Firebase.database.getReference("users/$username/availableQuizzes")

fun getQuizFromDB(quizId: String) =
    Firebase.database.getReference("quizzes/$quizId")

fun createUserNode(quizUser: QuizUser) =
    Firebase.database.getReference("users/${quizUser.username}").setValue(quizUser)

private fun matchByCategory(quiz: DataSnapshot?, category: String) = run {
    quiz?.let {
        return (quiz.exists() && quiz.hasChild("category") && quiz.child("category")
            .getValue(String::class.java) == category)
    }
    return@run false
}

private fun matchById(it: DataSnapshot, id: String) =
    it.key!! == id

fun getAvailableQuizzes(
    username: String,
    handler: (quiz: AppResult<List<Pair<String, Int?>>>) -> Unit
) =
    getUserNode(username).child("availableQuizzes")
        .addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.hasChildren()) {
                    val availableQuizList = mutableListOf<Pair<String, Int?>>()
                    snapshot.children.forEach {
                        it.key?.let { quizName ->
                            availableQuizList.add(quizName to getPercentage(it))
                        }
                    }
                    handler(AppResult.Success(availableQuizList))
                }
            }

            override fun onCancelled(error: DatabaseError) {
                handler(AppResult.Error(error.toException()))
            }
        })

fun getQuizzesCreated(
    username: String,
    quizCategory: QuizCategory,
    handler: (quiz: AppResult<Quiz>) -> Unit
) = getUserNode(username).child("availableQuizzes")
    .get().addOnSuccessListener { snapshot ->
        if (snapshot.hasChildren()) {
            snapshot.children.filter { quizSnapshot ->
                quizSnapshot.child("category")
                    .exists() && (quizSnapshot.child("category").value as String == quizCategory.name)
            }.also {
                if (it.isNotEmpty()) {
                    it.onEach { quizFiltered ->
                        quizFiltered.key?.let { key ->
                            val quiz = quizFiltered.getValue(Quiz::class.java)
                            getQuizDetails(key, quiz ?: Quiz(), quizCategory.name) { quiz ->
                                handler(quiz)
                            }
                        }
                            ?: throw IllegalArgumentException("Node cannot lack a key. ${snapshot.ref}")
                    }
                } else {
                    handler(AppResult.Error(NoSuchElementException("Unable to find any quizzes. Try creating one first.")))
                }
            }
        } else handler(AppResult.Error(NoSuchElementException("Unable to find any quizzes. Try creating one first.")))
    }.addOnFailureListener {
        handler(AppResult.Error(it))
    }

fun getQuizDetails(
    id: String,
    quizInitialDetails: Quiz,
    category: String,
    handler: (quiz: AppResult<Quiz>) -> Unit
) =
    Firebase.database.getReference("quizzes").get().addOnSuccessListener { snapshot ->
        fun throwNoElementException() {
            handler(AppResult.Error(NoSuchElementException("Unable to find any quizzes. Try creating one first.")))
        }
        try {
            if (snapshot.hasChildren()) {
                val quizRef = snapshot.child(id)
                if (quizRef.exists() && matchByCategory(quizRef, category)) {
                    val quizFull = quizInitialDetails.apply {
                        questions = quizRef.getValue(Quiz::class.java)?.questions
                    }
                    handler(AppResult.Success(quizFull))
                } else if (quizRef.exists().not()) {
                    Log.e("FirebaseRepo.kt", "getQuizDetails: $quizRef does not exist. ")
                    throw IllegalArgumentException("This node does not exist in QuizDB - ${quizRef.key}")
                } else throwNoElementException()
            } else {
                throwNoElementException()
            }
        } catch (e: NoSuchElementException) {
            throwNoElementException()
        }
    }.addOnFailureListener { handler(AppResult.Error(it)) }


fun createNewQuiz() = Firebase.database.reference.child("quizzes").push()