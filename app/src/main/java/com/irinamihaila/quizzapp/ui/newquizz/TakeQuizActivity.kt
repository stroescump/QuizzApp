package com.irinamihaila.quizzapp.ui.newquizz

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.widget.AppCompatButton
import androidx.lifecycle.lifecycleScope
import com.irinamihaila.quizzapp.R
import com.irinamihaila.quizzapp.databinding.ActivityTakeQuizBinding
import com.irinamihaila.quizzapp.models.Quiz
import com.irinamihaila.quizzapp.ui.base.BaseActivity
import com.irinamihaila.quizzapp.ui.dashboard.DashboardActivity
import com.irinamihaila.quizzapp.ui.leaderboard.LeaderboardBottomSheetFragment
import com.irinamihaila.quizzapp.utils.SharedPrefsUtils
import com.irinamihaila.quizzapp.utils.viewBinding
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TakeQuizActivity : BaseActivity() {
    override val binding by viewBinding(ActivityTakeQuizBinding::inflate)
    private val viewModel by viewModels<TakeQuizViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        intent.getBundleExtra("data")?.getParcelable<Quiz>("quiz")?.let { quiz ->
            viewModel.quiz = quiz
        }
        super.onCreate(savedInstanceState)
    }

    override fun onBackPressed() {
        navigateTo(DashboardActivity::class.java, true)
    }

    override fun setupListeners() {
        with(binding) {
            arrayOf(btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4).onEach { button ->
                button.setOnClickListener {
                    it.isEnabled = false
                    val correctAnswer = getCorrectAnswer()
                    colorAnswers(correctAnswer, button)
                }
            }
            btnSubmitAnswer.setOnClickListener {
                lifecycleScope.launch {
                    val quizFinishedString = getString(R.string.quizFinished)
                    if (isQuizFinished() && isDisplayLeaderboardNeeded(quizFinishedString).not()) {
                        btnSubmitAnswer.text = quizFinishedString
                        sendResults()
                    } else if (isDisplayLeaderboardNeeded(quizFinishedString)) {
                        displayLeaderboard()
                    } else {
                        viewModel.currentQuestion.update { it.inc() }
                    }
                }
            }
        }
    }

    override fun initViews() {
        binding.containerProfile.tvFirstName.text = SharedPrefsUtils(this).getFullName()
        if (quizQuestionSize() > viewModel.currentQuestion.value) {
            prepareNextQuestion(viewModel.currentQuestion.value)
        }
    }

    override fun setupObservers() {
        lifecycleScope.launch {
            viewModel.currentQuestion.collect { newPos ->
                if (quizQuestionSize() > newPos) {
                    prepareNextQuestion(newPos)
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun prepareNextQuestion(
        pos: Int
    ) {
        with(binding) {
            arrayOf(btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4).onEach {
                resetColor(it)
                it.isEnabled = true
            }
            tvQuestionsCounter.text = "${pos.plus(1)}/${viewModel.quiz.questions?.size}"
            val currentQuestion = viewModel.quiz.questions?.get(pos)
            tvQuizQuestion.text = currentQuestion?.question

            btnAnswer1.text = currentQuestion?.answer1
            btnAnswer2.text = currentQuestion?.answer2
            btnAnswer3.text = currentQuestion?.answer3
            btnAnswer4.text = currentQuestion?.answer4
        }
    }

    private fun ActivityTakeQuizBinding.colorAnswers(
        correctAnswer: String?,
        currentButton: AppCompatButton
    ) {
        arrayOf(btnAnswer1, btnAnswer2, btnAnswer3, btnAnswer4).onEach {
            if (it.id != currentButton.id) {
                if (it.text == correctAnswer) {
                    colorRightAnswer(it)
                } else {
                    colorWrongAnswer(it)
                }
            } else {
                if (it.text == correctAnswer) {
                    viewModel.correctAnswers = viewModel.correctAnswers.inc()
                }
                colorSelectedAnswer(it)
            }
        }
    }

    private fun colorSelectedAnswer(it: AppCompatButton) {
        it.setBackgroundResource(
            R.drawable.bg_custom_button_selected_answer
        )
    }

    private fun colorRightAnswer(it: AppCompatButton) {
        it.setBackgroundResource(
            R.drawable.bg_custom_button_right_answer
        )
    }

    private fun colorWrongAnswer(it: AppCompatButton) {
        it.setBackgroundResource(
            R.drawable.bg_custom_button_wrong_answer
        )
    }

    private fun resetColor(it: AppCompatButton) {
        it.setBackgroundResource(
            R.drawable.bg_custom_button
        )
    }

    private fun getCorrectAnswer() =
        viewModel.quiz.questions?.get(viewModel.currentQuestion.value)?.correctAnswer

    private fun quizQuestionSize() = viewModel.quiz.questions?.size!!

    private fun displayLeaderboard() {
        LeaderboardBottomSheetFragment.newInstance()
            .show(supportFragmentManager, LeaderboardBottomSheetFragment::class.java.simpleName)
    }

    private fun ActivityTakeQuizBinding.isDisplayLeaderboardNeeded(
        quizFinishedString: String
    ) = btnSubmitAnswer.text == quizFinishedString

    private fun isQuizFinished() =
        viewModel.currentQuestion.value.inc() == quizQuestionSize()

    private fun sendResults() {
        SharedPrefsUtils(this@TakeQuizActivity).also {
            it.getUsername()
                ?.let { username ->
                    viewModel.quiz.id?.let { quizId ->
                        viewModel.sendResults(
                            username,
                            quizId
                        )
                    }
                }
        }
    }
}