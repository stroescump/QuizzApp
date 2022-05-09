package com.irinamihaila.quizzapp.ui

import android.os.Bundle
import com.irinamihaila.quizzapp.databinding.ActivityMainBinding
import com.irinamihaila.quizzapp.ui.base.BaseActivity
import com.irinamihaila.quizzapp.utils.viewBinding

class MainActivity : BaseActivity() {
    override val binding by viewBinding(ActivityMainBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}