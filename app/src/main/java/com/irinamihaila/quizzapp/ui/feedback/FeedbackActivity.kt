package com.irinamihaila.quizzapp.ui.feedback

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.irinamihaila.quizzapp.R
import com.irinamihaila.quizzapp.databinding.ActivityFeedbackBinding
import com.irinamihaila.quizzapp.models.Quiz
import com.irinamihaila.quizzapp.models.UserType
import com.irinamihaila.quizzapp.models.UserType.AUTHOR
import com.irinamihaila.quizzapp.models.UserType.PLAYER
import com.irinamihaila.quizzapp.ui.base.BaseActivity
import com.irinamihaila.quizzapp.ui.dashboard.DashboardActivity
import com.irinamihaila.quizzapp.utils.*

class FeedbackActivity : BaseActivity() {
    override val binding by viewBinding(ActivityFeedbackBinding::inflate)

    private val quiz by lazy {
        intent.extras?.getBundle("data")?.getParcelable<Quiz>(Constants.QUIZ)
            ?: throw IllegalArgumentException("Need a valid quiz passed.")
    }

    private val userType by lazy {
        intent.extras?.getBundle("data")?.getParcelable<UserType>(Constants.USER_TYPE)
            ?: throw IllegalArgumentException("Need a valid user type passed.")
    }
    private val viewModel by viewModels<FeedbackViewModel> {
        FeedbackViewModel.Factory(
            SharedPrefsUtils(
                this
            )
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (userType == AUTHOR) {
            viewModel.retrieveFeedback(quiz)
        }
    }

    override fun setupListeners() {
        with(binding) {
            when (userType) {
                AUTHOR -> {}
                PLAYER -> {
                    btnSendFeedback.setOnClickListener {
                        viewModel.sendFeedback(etFeedback.value(), quiz)
                    }
                }
            }
        }
    }

    override fun initViews() {
        with(binding) {
            when (userType) {
                AUTHOR -> {
                    rvFeedback.adapter = FeedbackAdapter(mutableListOf())
                }
                PLAYER -> {
                    rvFeedback.hide()
                    etFeedback.show()
                    btnSendFeedback.show()
                }
            }
        }
    }

    override fun setupObservers() {
        with(binding) {
            lifecycleScope.launchWhenResumed {
                viewModel.uiState.collect { res ->
                    when (res) {
                        is AppResult.Error -> displayError(res.exception.localizedMessage)
                        AppResult.Progress -> showProgress()
                        is AppResult.Success -> {
                            hideProgress()
                            when (userType) {
                                AUTHOR -> res.successData?.feedback?.let {
                                    (binding.rvFeedback.adapter as FeedbackAdapter).refreshList(it)
                                }
                                PLAYER -> {
                                    displayInfo(getString(R.string.feedback_sent_success))
                                    navigateTo(
                                        DashboardActivity::class.java,
                                        isFinishActivity = true
                                    )
                                }
                            }
                        }
                        null -> {}
                    }
                }
            }
        }
    }
}