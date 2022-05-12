package com.irinamihaila.quizzapp.ui.dashboard

import android.os.Bundle
import androidx.viewbinding.ViewBinding
import com.irinamihaila.quizzapp.databinding.ActivityDashboardBinding
import com.irinamihaila.quizzapp.ui.base.BaseActivity
import com.irinamihaila.quizzapp.utils.viewBinding

class DashboardActivity : BaseActivity() {
    override val binding: ViewBinding by viewBinding(ActivityDashboardBinding::inflate)
    private val viewModel by lazy { DashboardViewModel() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}