package com.irinamihaila.quizzapp.ui.login

import android.graphics.Paint
import android.text.Editable
import com.irinamihaila.quizzapp.databinding.ActivityLoginBinding
import com.irinamihaila.quizzapp.ui.base.BaseActivity
import com.irinamihaila.quizzapp.ui.dashboard.DashboardActivity
import com.irinamihaila.quizzapp.ui.registration.RegisterActivity
import com.irinamihaila.quizzapp.utils.AppResult
import com.irinamihaila.quizzapp.utils.SharedPrefsUtils
import com.irinamihaila.quizzapp.utils.viewBinding

class LoginActivity : BaseActivity() {
    override val binding by viewBinding(ActivityLoginBinding::inflate)
    private val viewModel by lazy { AuthenticationViewModel(SharedPrefsUtils(this)) }

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
                is AppResult.Success -> {
                    binding.also {
                        hideProgress()
                        appResult.successData?.let { triple ->
                            SharedPrefsUtils(this).also {
                                it.saveFullName(triple.first)
                                it.saveUsername(triple.second)
                                it.saveUserType(triple.third)
                            }
                        }
                        navigateTo(DashboardActivity::class.java, true)
                    }
                }
            }
        }
    }

    override fun setupListeners() {
        with(binding) {
            btnLogin.setOnClickListener {
                viewModel.login(etUsername.text.toString(), etPassword.text.toString())
            }

            btnRegister.setOnClickListener {
                navigateTo(RegisterActivity::class.java)
            }
        }
        binding.btnLogin.performClick()
    }

    override fun initViews() {
        binding.btnRegister.paintFlags = Paint.UNDERLINE_TEXT_FLAG
        binding.etUsername.text = Editable.Factory().newEditable("a")
        binding.etPassword.text = Editable.Factory().newEditable("aa")
    }
}