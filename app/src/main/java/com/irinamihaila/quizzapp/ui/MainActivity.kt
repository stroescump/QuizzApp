package com.irinamihaila.quizzapp.ui

import com.irinamihaila.quizzapp.databinding.ActivityMainBinding
import com.irinamihaila.quizzapp.repo.getUserDetails
import com.irinamihaila.quizzapp.ui.base.BaseActivity
import com.irinamihaila.quizzapp.ui.dashboard.DashboardActivity
import com.irinamihaila.quizzapp.ui.login.LoginActivity
import com.irinamihaila.quizzapp.utils.AppResult
import com.irinamihaila.quizzapp.utils.SharedPrefsUtils
import com.irinamihaila.quizzapp.utils.viewBinding

class MainActivity : BaseActivity() {
    override val binding by viewBinding(ActivityMainBinding::inflate)

    override fun setupListeners() {}

    override fun initViews() {}

    override fun setupObservers() {
        SharedPrefsUtils(this).apply {
            getUsername().also { username ->
                if (username != null) {
                    getUserDetails(username) { res ->
                        when (res) {
                            is AppResult.Error -> {
                                navigateTo(LoginActivity::class.java)
                            }
                            AppResult.Progress -> showProgress()
                            is AppResult.Success -> {
                                hideProgress()
                                res.successData?.second?.let { saveFullName(it) }
                                res.successData?.first?.let { saveUserType(it) }
                                navigateTo(DashboardActivity::class.java, true)
                            }
                        }
                    }
                } else {
                    navigateTo(LoginActivity::class.java)
                }
            }
        }

    }

}