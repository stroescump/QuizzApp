package com.irinamihaila.quizzapp.ui.newquizz

import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.irinamihaila.quizzapp.databinding.ActivityCreateQuizBinding
import com.irinamihaila.quizzapp.ui.base.BaseActivity
import com.irinamihaila.quizzapp.ui.dashboard.QuizCategory
import com.irinamihaila.quizzapp.utils.SharedPrefsUtils
import com.irinamihaila.quizzapp.utils.viewBinding
import kotlinx.coroutines.flow.collectLatest

class CreateQuizActivity : BaseActivity() {
    override val binding by viewBinding(ActivityCreateQuizBinding::inflate)
    private val viewModel by lazy { QuizViewModel(SharedPrefsUtils(this)) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initViews()
        setupListeners()
        val quizCategory =
            intent.extras?.getBundle("data")?.getParcelable<QuizCategory>("quizCategory")
        viewModel.createQuiz(
            quizCategory ?: throw IllegalStateException("Must have a valid quiz category")
        )
    }

    override fun setupListeners() {
        binding.btnAddMore.setOnClickListener {
            viewModel.currentQuizId.value?.let {
                CreateQuizBottomSheetFragment.newInstance(it)
                    .show(
                        supportFragmentManager,
                        CreateQuizBottomSheetFragment::class.java.simpleName
                    )
            }
        }
    }

    override fun initViews() {
        binding.rvNewQuestions.adapter = QuizItemAdapter(mutableListOf(), false)
    }

    override fun setupObservers() {
        lifecycleScope.launchWhenResumed {
            viewModel.uiState.collectLatest { res ->
                res?.let {
                    when (it.first) {
                        true -> displayInfo("Success.")
                        false -> displayError(it.second)
                    }
                }
            }
        }
    }
}