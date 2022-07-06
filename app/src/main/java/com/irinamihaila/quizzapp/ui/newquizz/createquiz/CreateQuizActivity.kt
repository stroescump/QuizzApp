package com.irinamihaila.quizzapp.ui.newquizz.createquiz

import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.irinamihaila.quizzapp.databinding.ActivityCreateQuizBinding
import com.irinamihaila.quizzapp.models.Question
import com.irinamihaila.quizzapp.ui.base.BaseActivity
import com.irinamihaila.quizzapp.ui.dashboard.QuizCategory
import com.irinamihaila.quizzapp.ui.newquizz.QuizItemAdapter
import com.irinamihaila.quizzapp.ui.newquizz.takequiz.QuizViewModel
import com.irinamihaila.quizzapp.utils.*
import com.irinamihaila.quizzapp.utils.Constants.IS_EDIT
import com.irinamihaila.quizzapp.utils.Constants.IS_NEW_QUIZ
import com.irinamihaila.quizzapp.utils.Constants.QUIZ_ID
import com.irinamihaila.quizzapp.utils.Constants.dateFormatter
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import java.util.*

class CreateQuizActivity : BaseActivity() {
    override val binding by viewBinding(ActivityCreateQuizBinding::inflate)
    private val viewModel by viewModels<QuizViewModel> {
        QuizViewModel.Factory(
            SharedPrefsUtils(this)
        )
    }
    private var isEditMode = false
    private var isNewQuiz = false

    override fun onCreate(savedInstanceState: Bundle?) {
        intent.extras?.getBundle("data")?.let {
            isEditMode = it.getBoolean(IS_EDIT, false)
            isNewQuiz = it.getBoolean(IS_NEW_QUIZ, false)
            it.getString(QUIZ_ID, null)
                .also { quizId -> viewModel.currentQuizId.update { quizId } }
            if (isEditMode.not() || isNewQuiz) {
                val quizCategory = it.getParcelable<QuizCategory>(Constants.QUIZ_CATEGORY)
                viewModel.createQuiz(
                    quizCategory ?: throw IllegalStateException("Must have a valid quiz category"),
                    dateFormatter.format(Calendar.getInstance().time)
                )
            } else {
                viewModel.currentQuizId.value?.let { id ->
                    viewModel.getQuestionsFromSelectedQuiz(
                        id
                    )
                }
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
        with(binding) {
            rvNewQuestions.adapter =
                QuizItemAdapter(mutableListOf(), isEditMode) { question, pos ->
                    showQuestionsBottomSheet(question, pos)
                }
            if (isNewQuiz) {
                tvNoQuestions.show()
            }
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