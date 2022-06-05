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
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.launch

class SeeAvailableQuizActivity : BaseActivity() {
    override val binding by viewBinding(ActivitySeeAvailableQuizBinding::inflate)
    private val viewModel by viewModels<SeeAvailableQuizViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getParcelableExtra<Bundle>("data")?.also {
            viewModel.quizCategory = it.getParcelable("data") ?: QuizCategory.QuizGeneralKnowledge
            SharedPrefsUtils(this).getUsername()
                ?.let { username -> viewModel.getQuizzesForUser(username) }
        }
    }

    override fun setupListeners() {

    }

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
            viewModel.quizzesFlow.collectIndexed { _, res ->
                when (res) {
                    is AppResult.Error -> {
                        hideProgress()
                        displayError(res.exception.localizedMessage)
                    }
                    AppResult.Progress -> {
                        showProgress()
                    }
                    is AppResult.Success -> {
                        hideProgress()
                        res.successData?.let { quiz ->
                            (binding.rvAvailableQuizzez.adapter as QuizAvailableAdapter).addToList(
                                quiz
                            )
                        }
                    }
                }
            }
        }

        viewModel.errorLiveData.observe(this) {
            hideProgress()
            displayError(it.localizedMessage)
        }
    }
}