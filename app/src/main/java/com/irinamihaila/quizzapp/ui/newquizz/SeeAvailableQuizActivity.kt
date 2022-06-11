package com.irinamihaila.quizzapp.ui.newquizz

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.irinamihaila.quizzapp.R
import com.irinamihaila.quizzapp.databinding.ActivitySeeAvailableQuizBinding
import com.irinamihaila.quizzapp.ui.base.BaseActivity
import com.irinamihaila.quizzapp.ui.dashboard.QuizCategory
import com.irinamihaila.quizzapp.utils.AppResult
import com.irinamihaila.quizzapp.utils.SharedPrefsUtils
import com.irinamihaila.quizzapp.utils.viewBinding
import kotlinx.coroutines.launch

class SeeAvailableQuizActivity : BaseActivity() {
    override val binding by viewBinding(ActivitySeeAvailableQuizBinding::inflate)
    private val viewModel by viewModels<SeeAvailableQuizViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getParcelableExtra<Bundle>("data")?.also {
            viewModel.quizCategory = it.getParcelable("quizCategory")
                ?: throw IllegalArgumentException("Must have a valid quiz category.")
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
    }

    override fun setupListeners() {}

    override fun initViews() {
        with(binding) {
            rvAvailableQuizzez.adapter = QuizAvailableAdapter(mutableListOf()) { quiz ->
                if (quiz.isRedo == true || quiz.percentage == null) {
                    navigateTo(
                        TakeQuizActivity::class.java,
                        extras = Bundle().also { it.putParcelable("quiz", quiz) })
                } else displayError(getString(R.string.error_take_quiz_once))
            }
        }
    }

    override fun setupObservers() {
        lifecycleScope.launch {
            viewModel.quizzesFlow.collect { res ->
                when (res) {
                    is AppResult.Error -> handleErrors(res)
                    AppResult.Progress -> showProgress()
                    is AppResult.Success -> {
                        hideProgress()
                        res.successData?.let { quiz ->
                            getQuizAdapter().addToList(quiz)
                        }
                    }
                }
            }
        }
    }

    private fun handleErrors(res: AppResult.Error<Quiz>) {
        val message = res.exception.localizedMessage
        if (message == getString(R.string.error_unable_to_find_any_quizzes)
            && viewModel.userType == AUTHOR
        ) {
            AlertDialog.Builder(this@SeeAvailableQuizActivity)
                .setMessage("Would you like to create a quiz?").setButton(
                    AlertDialogButton.PositiveButton
                ) {
                    navigateTo(
                        CreateQuizActivity::class.java,
                        true,
                        Bundle().also { it.putParcelable("quizCategory", viewModel.quizCategory) })
                }.setButton(
                    AlertDialogButton.NegativeButton
                ) {
                    navigateTo(DashboardActivity::class.java, true)
                }.create().show()
        } else displayError(message)
    }

    private fun getQuizAdapter() = (binding.rvAvailableQuizzez.adapter as QuizAvailableAdapter)
}