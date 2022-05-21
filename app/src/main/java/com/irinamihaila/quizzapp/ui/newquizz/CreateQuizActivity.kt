package com.irinamihaila.quizzapp.ui.newquizz

import android.os.Bundle
import com.irinamihaila.quizzapp.databinding.ActivityCreateQuizBinding
import com.irinamihaila.quizzapp.ui.base.BaseActivity
import com.irinamihaila.quizzapp.utils.viewBinding

class CreateQuizActivity : BaseActivity() {
    override val binding by viewBinding(ActivityCreateQuizBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        setupListeners()
    }

    override fun setupListeners() {
        binding.btnAddMore.setOnClickListener {
            CreateQuizBottomSheetFragment.newInstance()
                .show(supportFragmentManager, CreateQuizBottomSheetFragment::class.java.simpleName)
        }
    }

    override fun initViews() {
        binding.rvNewQuestions.adapter = QuizItemAdapter(mutableListOf(), false)
    }

}