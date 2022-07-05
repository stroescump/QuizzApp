package com.irinamihaila.quizzapp.ui.newquizz.createquiz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.irinamihaila.quizzapp.R
import com.irinamihaila.quizzapp.databinding.FragmentQuizDetailsBottomSheetBinding
import com.irinamihaila.quizzapp.models.Quiz
import com.irinamihaila.quizzapp.ui.newquizz.QuizAvailableAdapter
import com.irinamihaila.quizzapp.ui.newquizz.availablequizzes.SeeAvailableQuizActivity
import com.irinamihaila.quizzapp.ui.newquizz.takequiz.QuizViewModel
import com.irinamihaila.quizzapp.utils.SharedPrefsUtils
import com.irinamihaila.quizzapp.utils.value
import kotlinx.coroutines.flow.collectLatest

private const val ARG_QUIZ = "QUIZ"
private const val ARG_QUIZ_POS = "QUIZ_POS"
private const val DEFAULT_VALUE = 0

class QuizDetailsBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var quiz: Quiz
    private var quizPos: Int = DEFAULT_VALUE
    private val viewModel by activityViewModels<QuizViewModel> {
        QuizViewModel.Factory(
            SharedPrefsUtils(requireContext())
        )
    }
    private lateinit var binding: FragmentQuizDetailsBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            quizPos = it.getInt(ARG_QUIZ_POS)
            quiz = it.getParcelable(ARG_QUIZ)
                ?: throw IllegalArgumentException("Need a quiz passed as params.")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentQuizDetailsBottomSheetBinding.inflate(layoutInflater, null, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupObservers()
    }

    private fun setupObservers() {
        lifecycleScope.launchWhenResumed {
            viewModel.uiState.collectLatest { uiState ->
                uiState?.let {
                    when (it.first) {
                        true -> dismiss()
                        false -> displayError(it)
                    }
                }
            }
        }
    }

    private fun displayError(it: Pair<Boolean, String?>) {
        Snackbar.make(
            binding.root,
            it.second ?: getText(R.string.generic_error),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun setupListeners() {
        with(binding) {
            btnUpdateQuiz.setOnClickListener {
                quiz.apply {
                    name = etQuizName.value()
                    issuedDate = etCreationDate.value()
                    isRedo = switchIsRedo.isChecked

                }
                getAdapter().updateItem(quiz, quizPos)
                viewModel.updateQuiz(quiz)
            }
        }
    }

    private fun getAdapter() =
        ((requireActivity() as SeeAvailableQuizActivity).findViewById<RecyclerView>(R.id.rvAvailableQuizzez).adapter as QuizAvailableAdapter)

    companion object {
        @JvmStatic
        fun newInstance(quiz: Quiz, quizPos: Int) =
            QuizDetailsBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(ARG_QUIZ, quiz)
                    putInt(ARG_QUIZ_POS, quizPos)
                }
            }
    }
}