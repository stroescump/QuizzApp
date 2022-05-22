package com.irinamihaila.quizzapp.ui.newquizz

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.irinamihaila.quizzapp.R
import com.irinamihaila.quizzapp.databinding.FragmentCreateQuizBottomSheetBinding
import com.irinamihaila.quizzapp.models.Question
import com.irinamihaila.quizzapp.utils.SharedPrefsUtils
import com.irinamihaila.quizzapp.utils.value


class CreateQuizBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentCreateQuizBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val viewModel by lazy { QuizViewModel(SharedPrefsUtils(requireContext())) }

    override fun getTheme(): Int {
        return R.style.CustomBottomSheetDialog
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateQuizBottomSheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(binding) {
            btnAddQuestion.setOnClickListener {
                val correctAnswer = getCorrectAnswer()
                val newQuestion = Question(
                    question = layoutQuestionItem.etNewQuestion.value(),
                    answer1 = layoutQuestionItem.etAnswer1.value(),
                    answer2 = layoutQuestionItem.etAnswer2.value(),
                    answer3 = layoutQuestionItem.etAnswer3.value(),
                    answer4 = layoutQuestionItem.etAnswer4.value(),
                    correctAnswer = correctAnswer
                )
                getAdapter().addItem(newQuestion)
                viewModel.uploadQuestion(newQuestion)
                dismiss()
            }
        }
    }

    companion object {
        // TODO: Customize parameters
        fun newInstance(): CreateQuizBottomSheetFragment =
            CreateQuizBottomSheetFragment().apply {
                arguments = Bundle().apply {

                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getAdapter() =
        (requireActivity().findViewById<RecyclerView>(R.id.rvNewQuestions).adapter as QuizItemAdapter)

    private fun getCorrectAnswer() =
        with(binding.layoutQuestionItem) {
            when (rbGroup.checkedRadioButtonId) {
                rbAnswer1.id -> etAnswer1.value()
                rbAnswer2.id -> etAnswer2.value()
                rbAnswer3.id -> etAnswer3.value()
                rbAnswer4.id -> etAnswer4.value()
                else -> ""
            }
        }
}