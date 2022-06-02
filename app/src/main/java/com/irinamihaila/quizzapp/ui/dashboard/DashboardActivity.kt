package com.irinamihaila.quizzapp.ui.dashboard

import android.os.Bundle
import com.irinamihaila.quizzapp.databinding.ActivityDashboardBinding
import com.irinamihaila.quizzapp.ui.base.BaseActivity
import com.irinamihaila.quizzapp.utils.SharedPrefsUtils
import com.irinamihaila.quizzapp.utils.viewBinding

class DashboardActivity : BaseActivity() {
    override val binding by viewBinding(ActivityDashboardBinding::inflate)
    private val viewModel by lazy { DashboardViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun setupListeners() {

    }

    override fun initViews() {
        with(binding) {
            tvFirstName.text = SharedPrefsUtils(this@DashboardActivity).getUserId()
        }
    }

    override fun setupObservers() {

    }
}