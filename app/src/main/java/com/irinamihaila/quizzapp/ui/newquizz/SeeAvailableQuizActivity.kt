package com.irinamihaila.quizzapp.ui.newquizz

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.irinamihaila.quizzapp.R
import com.irinamihaila.quizzapp.databinding.ActivitySeeAvailableQuizBinding
import com.irinamihaila.quizzapp.models.Quiz
import com.irinamihaila.quizzapp.models.UserType.*
import com.irinamihaila.quizzapp.ui.base.BaseActivity
import com.irinamihaila.quizzapp.ui.dashboard.DashboardActivity
import com.irinamihaila.quizzapp.ui.feedback.FeedbackActivity
import com.irinamihaila.quizzapp.utils.*
import com.irinamihaila.quizzapp.utils.Constants.IS_EDIT
import com.irinamihaila.quizzapp.utils.Constants.IS_NEW_QUIZ
import com.irinamihaila.quizzapp.utils.Constants.QUIZ
import com.irinamihaila.quizzapp.utils.Constants.QUIZ_CATEGORY
import com.irinamihaila.quizzapp.utils.Constants.QUIZ_ID
import com.irinamihaila.quizzapp.utils.Constants.USER_TYPE
import kotlinx.coroutines.launch

class SeeAvailableQuizActivity : BaseActivity() {
    override val binding by viewBinding(ActivitySeeAvailableQuizBinding::inflate)
    private val viewModel by viewModels<SeeAvailableQuizViewModel> {
        SeeAvailableQuizViewModel.Factory(SharedPrefsUtils(this))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        intent.getParcelableExtra<Bundle>("data")?.also {
            viewModel.quizCategory = it.getParcelable(QUIZ_CATEGORY)
                ?: throw IllegalArgumentException(getString(R.string.error_valid_quiz_category))
            SharedPrefsUtils(this).apply {
                getUsername()?.let { username ->
                    getUserType()?.let { userType ->
                        valueOf(userType).also { type ->
                            viewModel.userType = type
                            viewModel.getQuizzesForUser(username, type)
                        }
                    }
                }
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun setupListeners() {
        with(binding) {
            btnAddMore.setOnClickListener {
                navigateTo(
                    CreateQuizActivity::class.java,
                    extras = Bundle().also {
                        it.putParcelable(QUIZ_CATEGORY, viewModel.quizCategory)
                        it.putBoolean(IS_EDIT, true)
                        it.putBoolean(IS_NEW_QUIZ, true)
                    })
            }
        }
    }

    override fun initViews() {
        with(binding) {
            if (viewModel.userType == PLAYER) {
                btnAddMore.hide()
            }
            rvAvailableQuizzez.adapter =
                QuizAvailableAdapter(
                    viewModel.userType == AUTHOR,
                    mutableListOf(),
                    handleOnQuizClick(),
                    handleOnQuizDelete(),
                    handleOnQuizEmpty(),
                    handleOnFeedbackClickListener()
                )
        }
    }

    private fun handleOnFeedbackClickListener() = { quiz: Quiz ->
        navigateTo(FeedbackActivity::class.java, isFinishActivity = true, extras = Bundle().also {
            it.putParcelable(USER_TYPE, viewModel.userType)
            it.putParcelable(QUIZ, quiz)
        })
    }

    override fun setupObservers() {
        lifecycleScope.launchWhenResumed {
            launch {
                viewModel.quizzesFlow.collect { res ->
                    when (res) {
                        is AppResult.Error -> handleErrors(res)
                        AppResult.Progress -> showProgress()
                        is AppResult.Success -> {
                            hideProgress()
                            res.successData?.let { quiz ->
                                binding.tvNoQuizzes.hide()
                                getQuizAdapter().addToList(quiz)
                            }
                        }
                    }
                }
            }

            launch {
                viewModel.uiEvents.collect { res ->
                    when (res) {
                        is AppResult.Error -> handleErrors(res)
                        AppResult.Progress -> showProgress()
                        is AppResult.Success -> {
                            hideProgress()
                            displayInfo(getString(R.string.delete_successful))
                        }
                    }
                }
            }
        }
    }

    private fun handleOnQuizClick() = { quiz: Quiz, quizPos: Int, isLongPress: Boolean ->
        when (isLongPress) {
            true -> handleLongClickQuiz(quiz, quizPos)
            false -> handleClickQuiz(quiz)
        }
    }

    private fun handleClickQuiz(quiz: Quiz) {
        if (isPlayer(quiz)) {
            navigateTo(
                TakeQuizActivity::class.java,
                true,
                extras = Bundle().also { it.putParcelable("quiz", quiz) })
        } else if (viewModel.userType == AUTHOR) {
            navigateTo(
                CreateQuizActivity::class.java,
                true,
                extras = Bundle().also {
                    it.putParcelable(QUIZ_CATEGORY, viewModel.quizCategory)
                    it.putBoolean(IS_EDIT, true)
                    it.putString(QUIZ_ID, quiz.id)
                })
        } else displayError(getString(R.string.error_take_quiz_once))
    }

    private fun handleLongClickQuiz(quiz: Quiz, quizPos: Int) {
        if (viewModel.userType == AUTHOR) {
            QuizDetailsBottomSheetFragment.newInstance(quiz, quizPos)
                .show(supportFragmentManager, QuizDetailsBottomSheetFragment::class.java.simpleName)
        }
    }

    private fun handleOnQuizDelete() = { quiz: Quiz ->
        viewModel.deleteQuiz(quiz)
    }

    private fun handleOnQuizEmpty(): () -> Unit = {
        binding.tvNoQuizzes.show()
    }

    private fun handleErrors(res: AppResult.Error<Quiz>) {
        val message = res.exception.localizedMessage
        if (message == getString(R.string.error_unable_to_find_any_quizzes)
            && viewModel.userType == AUTHOR
        ) {
            AlertDialog.Builder(this@SeeAvailableQuizActivity)
                .setMessage(getString(R.string.would_you_like_to_create_quiz)).setButton(
                    AlertDialogButton.PositiveButton
                ) {
                    navigateTo(
                        CreateQuizActivity::class.java,
                        true,
                        Bundle().also {
                            it.putBoolean(IS_EDIT, true)
                            it.putBoolean(IS_NEW_QUIZ, true)
                            it.putParcelable(QUIZ_CATEGORY, viewModel.quizCategory)
                        })
                }.setButton(
                    AlertDialogButton.NegativeButton
                ) {
                    navigateTo(DashboardActivity::class.java, true)
                }.create().show()
        } else displayError(message)
    }

    private fun isPlayer(quiz: Quiz) =
        (quiz.isRedo == true || quiz.percentage == null) && viewModel.userType == PLAYER

    private fun getQuizAdapter() = (binding.rvAvailableQuizzez.adapter as QuizAvailableAdapter)
}