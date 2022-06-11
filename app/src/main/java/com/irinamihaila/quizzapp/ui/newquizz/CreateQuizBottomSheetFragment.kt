package com.irinamihaila.quizzapp.ui.newquizz

import android.content.DialogInterface
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
import com.irinamihaila.quizzapp.utils.toEditable
import com.irinamihaila.quizzapp.utils.value


class CreateQuizBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentCreateQuizBottomSheetBinding? = null
    private val binding get() = _binding!!
    private val viewModel by lazy { QuizViewModel(SharedPrefsUtils(requireContext())) }
    private val quizId by lazy {
        arguments?.getString("quizId") ?: throw IllegalStateException("Must have a valid quiz id.")
    }
    private val questionToBeUpdated by lazy { arguments?.getParcelable<Question>("questionUpdate") }
    private val questionPosition by lazy { arguments?.getInt("questionPosition") }

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
        initViews(questionToBeUpdated)
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

                questionToBeUpdated?.let {
                    questionPosition?.let { pos -> getAdapter().updateQuestion(pos, newQuestion) }
                    viewModel.updateQuestion(it, newQuestion, quizId)
                } ?: run {
                    getAdapter().addItem(newQuestion)
                    viewModel.uploadQuestion(newQuestion, quizId)
                }
                dismiss()
            }
        }
    }

    private fun initViews(questionToBeUpdated: Question?) {
        questionToBeUpdated?.let { question ->
            with(binding) {
                btnAddQuestion.text = getString(R.string.update_question)
                layoutQuestionItem.etNewQuestion.text = question.question?.toEditable()
                layoutQuestionItem.etAnswer1.text = question.answer1?.toEditable()
                layoutQuestionItem.etAnswer2.text = question.answer2?.toEditable()
                layoutQuestionItem.etAnswer3.text = question.answer3?.toEditable()
                layoutQuestionItem.etAnswer4.text = question.answer4?.toEditable()
            }
        }
    }

    companion object {
        fun newInstance(
            quizId: String,
            questionToBeUpdated: Question?,
            pos: Int?
        ): CreateQuizBottomSheetFragment =
            CreateQuizBottomSheetFragment().apply {
                arguments = Bundle().apply {
                    putString("quizId", quizId)
                    questionToBeUpdated?.let {
                        putParcelable("questionUpdate", it)
                    }
                    pos?.let { putInt("questionPosition", pos) }
                }
            }
    }

    override fun onDismiss(dialog: DialogInterface) {

        super.onDismiss(dialog)
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