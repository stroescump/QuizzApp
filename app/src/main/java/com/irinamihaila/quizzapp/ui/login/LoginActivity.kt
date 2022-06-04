package com.irinamihaila.quizzapp.ui.login

import android.os.Bundle
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.irinamihaila.quizzapp.databinding.ActivityLoginBinding
import com.irinamihaila.quizzapp.models.Question
import com.irinamihaila.quizzapp.models.Quiz
import com.irinamihaila.quizzapp.ui.base.BaseActivity
import com.irinamihaila.quizzapp.ui.dashboard.DashboardActivity
import com.irinamihaila.quizzapp.ui.registration.RegisterActivity
import com.irinamihaila.quizzapp.utils.AppResult
import com.irinamihaila.quizzapp.utils.SharedPrefsUtils
import com.irinamihaila.quizzapp.utils.viewBinding

class LoginActivity : BaseActivity() {
    override val binding by viewBinding(ActivityLoginBinding::inflate)
    private val viewModel by lazy { AuthenticationViewModel(SharedPrefsUtils(this)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        Firebase.database.reference.child("quizzes").setValue(
//            listOf(
//                Quiz(
//                    "idex", "name1", "GENERAL_KNOWLEDGE", "04.06.2022", true, listOf(
//                        Question("Ce faceti?", "ans1", "ans2", "ans3", "ans4", "ans3"),
//                        Question("Ce faceti?", "ans1", "ans2", "ans3", "ans4", "ans3"),
//                        Question("Ce faceti?", "ans1", "ans2", "ans3", "ans4", "ans3")
//                    )
//                ),
//                Quiz(
//                    "idex", "name1", "GENERAL_KNOWLEDGE", "04.06.2022", true, listOf(
//                        Question("Ce faceti?", "ans1", "ans2", "ans3", "ans4", "ans3"),
//                        Question("Ce faceti?", "ans1", "ans2", "ans3", "ans4", "ans3"),
//                        Question("Ce faceti?", "ans1", "ans2", "ans3", "ans4", "ans3")
//                    )
//                ),
//                Quiz(
//                    "idex", "name1", "GENERAL_KNOWLEDGE", "04.06.2022", true, listOf(
//                        Question("Ce faceti?", "ans1", "ans2", "ans3", "ans4", "ans3"),
//                        Question("Ce faceti?", "ans1", "ans2", "ans3", "ans4", "ans3"),
//                        Question("Ce faceti?", "ans1", "ans2", "ans3", "ans4", "ans3")
//                    )
//                )
//            )
//        )
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
                is AppResult.Success -> {
                    binding.also {
                        hideProgress()
                        appResult.successData?.let { pair ->
                            SharedPrefsUtils(this).also {
                                it.saveFullName(pair.first)
                                it.saveUsername(pair.second)
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
    }

    override fun initViews() {

    }
}