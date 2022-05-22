package com.irinamihaila.quizzapp.ui.login

import android.os.Bundle
import com.irinamihaila.quizzapp.databinding.ActivityLoginBinding
import com.irinamihaila.quizzapp.ui.base.BaseActivity
import com.irinamihaila.quizzapp.utils.AppResult
import com.irinamihaila.quizzapp.utils.SharedPrefsUtils
import com.irinamihaila.quizzapp.utils.viewBinding

class LoginActivity : BaseActivity() {
    override val binding by viewBinding(ActivityLoginBinding::inflate)
    private val viewModel by lazy { AuthenticationViewModel(SharedPrefsUtils(this)) }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupObservers()
    }

    override fun setupObservers() {
        viewModel.uiStateLiveData.observe(this) { appResult ->
            when (appResult) {
                is AppResult.Error -> {
                    displayError(appResult.exception.localizedMessage)
                }
                AppResult.Progress -> {}
                is AppResult.Retry -> {}
                is AppResult.Success -> {
                    binding.also {
                        it.etPassword.text?.clear()
                        it.etUsername.text?.clear()
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
        }
    }

    override fun initViews() {

    }
}