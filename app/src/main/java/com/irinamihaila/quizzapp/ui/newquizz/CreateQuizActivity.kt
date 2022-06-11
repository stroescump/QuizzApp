package com.irinamihaila.quizzapp.ui.newquizz

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.irinamihaila.quizzapp.databinding.ActivityCreateQuizBinding
import com.irinamihaila.quizzapp.models.Question
import com.irinamihaila.quizzapp.ui.base.BaseActivity
import com.irinamihaila.quizzapp.ui.dashboard.QuizCategory
import com.irinamihaila.quizzapp.utils.AppResult
import com.irinamihaila.quizzapp.utils.SharedPrefsUtils
import com.irinamihaila.quizzapp.utils.viewBinding
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update

class CreateQuizActivity : BaseActivity() {
    override val binding by viewBinding(ActivityCreateQuizBinding::inflate)
    private val viewModel by viewModels<QuizViewModel> {
        QuizViewModel.Factory(
            SharedPrefsUtils(this)
        )
    }
    private var isEditMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        intent.extras?.getBundle("data")?.let {
            isEditMode = it.getBoolean("isEdit", false)
            it.getString("quizId", null)
                .also { quizId -> viewModel.currentQuizId.update { quizId } }
            val isEmptyQuiz = it.getBoolean("IS_QUIZ_EMPTY", false)
            if (isEditMode.not() || isEmptyQuiz.not()) {
                val quizCategory = it.getParcelable<QuizCategory>("quizCategory")
                viewModel.createQuiz(
                    quizCategory ?: throw IllegalStateException("Must have a valid quiz category")
                )
            } else {
                viewModel.currentQuizId.value?.let { id -> viewModel.getQuestionsFromSelectedQuiz(id) }
            }
        }
        super.onCreate(savedInstanceState)
    }

    override fun setupListeners() {
        binding.btnAddMore.setOnClickListener {
            showQuestionsBottomSheet()
        }
    }

    private fun showQuestionsBottomSheet(questionToBeUpdated: Question? = null, pos: Int? = null) =
        viewModel.currentQuizId.value?.let {
            return@let CreateQuizBottomSheetFragment.newInstance(it, questionToBeUpdated, pos)
                .also { bottomSheet ->
                    bottomSheet.show(
                        supportFragmentManager,
                        CreateQuizBottomSheetFragment::class.java.simpleName
                    )
                }
        }

    private fun getQuizAdapter() = (binding.rvNewQuestions.adapter as QuizItemAdapter)

    override fun initViews() {
        binding.rvNewQuestions.adapter =
            QuizItemAdapter(mutableListOf(), isEditMode) { question, pos ->
                showQuestionsBottomSheet(question, pos)
            }
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

        if (isEditMode) {
            viewModel.editQuizLiveData.observe(this) { res ->
                when (res) {
                    is AppResult.Error -> displayError(res.exception.localizedMessage)
                    AppResult.Progress -> showProgress()
                    is AppResult.Success -> {
                        hideProgress()
                        res.successData?.apply {
                            questions?.let {
                                val questionsList = it.filterNotNull()
                                getQuizAdapter().refreshList(questionsList)
                            }
                        }
                    }
                }
            }
        }
    }
}