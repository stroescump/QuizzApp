package com.irinamihaila.quizzapp.ui.newquizz

import android.os.Bundle
import com.irinamihaila.quizzapp.databinding.ActivitySeeAvailableQuizBinding
import com.irinamihaila.quizzapp.ui.base.BaseActivity
import com.irinamihaila.quizzapp.ui.dashboard.QuizCategory
import com.irinamihaila.quizzapp.utils.AppResult
import com.irinamihaila.quizzapp.utils.SharedPrefsUtils
import com.irinamihaila.quizzapp.utils.viewBinding

class SeeAvailableQuizActivity : BaseActivity() {
    override val binding by viewBinding(ActivitySeeAvailableQuizBinding::inflate)
    private val viewModel by lazy { defaultViewModelProviderFactory.create(SeeAvailableQuizViewModel::class.java) }

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
                navigateTo(
                    TakeQuizActivity::class.java,
                    extras = Bundle().also { it.putParcelable("quiz", quiz) })
            }
        }
    }

    override fun setupObservers() {
        viewModel.availableQuizzesLiveData.observe(this) {
            when (it) {
                is AppResult.Error -> {
                    hideProgress()
                    displayError(it.exception.localizedMessage)
                }
                AppResult.Progress -> {
                    showProgress()
                }
                is AppResult.Success -> {
                    hideProgress()
                    it.successData?.let { quiz ->
                        (binding.rvAvailableQuizzez.adapter as QuizAvailableAdapter).addToList(quiz)
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