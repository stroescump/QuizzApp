package com.irinamihaila.quizzapp.ui.newquizz

import android.os.Bundle
import com.irinamihaila.quizzapp.databinding.ActivityTakeQuizBinding
import com.irinamihaila.quizzapp.ui.base.BaseActivity
import com.irinamihaila.quizzapp.utils.viewBinding

class TakeQuizActivity : BaseActivity() {
    override val binding by viewBinding(ActivityTakeQuizBinding::inflate)
    private val viewModel by lazy { defaultViewModelProviderFactory.create(TakeQuizViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun setupListeners() {
        TODO("Not yet implemented")
    }

    override fun initViews() {
        TODO("Not yet implemented")
    }


}