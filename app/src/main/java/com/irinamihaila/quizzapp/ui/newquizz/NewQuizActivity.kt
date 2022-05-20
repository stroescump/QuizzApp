package com.irinamihaila.quizzapp.ui.newquizz

import android.os.Bundle
import com.irinamihaila.quizzapp.databinding.ActivityNewQuizBinding
import com.irinamihaila.quizzapp.ui.base.BaseActivity
import com.irinamihaila.quizzapp.utils.viewBinding

class NewQuizActivity : BaseActivity() {
    override val binding by viewBinding(ActivityNewQuizBinding::inflate)
    private val viewModel by lazy { defaultViewModelProviderFactory.create(NewQuizViewModel::class.java) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }


}