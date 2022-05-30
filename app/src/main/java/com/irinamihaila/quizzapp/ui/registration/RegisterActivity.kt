package com.irinamihaila.quizzapp.ui.registration

import android.os.Bundle
import com.irinamihaila.quizzapp.databinding.ActivityRegisterBinding
import com.irinamihaila.quizzapp.models.QuizUser
import com.irinamihaila.quizzapp.ui.base.BaseActivity
import com.irinamihaila.quizzapp.ui.dashboard.DashboardActivity
import com.irinamihaila.quizzapp.ui.login.AuthenticationViewModel
import com.irinamihaila.quizzapp.utils.AppResult
import com.irinamihaila.quizzapp.utils.SharedPrefsUtils
import com.irinamihaila.quizzapp.utils.value
import com.irinamihaila.quizzapp.utils.viewBinding

class RegisterActivity : BaseActivity() {

    override val binding by viewBinding(ActivityRegisterBinding::inflate)
    private val viewModel by lazy { AuthenticationViewModel(SharedPrefsUtils(this)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setupListeners() {
        with(binding) {
            btnRegister.setOnClickListener {
                val role = when (rbGroupUserType.checkedRadioButtonId) {
                    rbPlayer.id -> "PLAYER"
                    rbAuthor.id -> "AUTHOR"
                    else -> throw IllegalArgumentException("Cannot have other role than PLAYER / AUTHOR")
                }
                viewModel.register(
                    QuizUser(
                        etFullName.value(),
                        etUsername.value(),
                        etPassword.value(),
                        "",
                        role
                    )
                )
            }
        }
    }

    override fun initViews() {
    }

    override fun setupObservers() {
        viewModel.uiStateLiveData.observe(this) { appResult ->
            when (appResult) {
                is AppResult.Error -> {
                    hideProgress()
                    displayError(appResult.exception.localizedMessage)
                }
                AppResult.Progress -> {
                    showProgress()
                }
                is AppResult.Retry -> {}
                is AppResult.Success -> {
                    hideProgress()
                    navigateTo(DashboardActivity::class.java, true)
                }
            }
        }
    }
}