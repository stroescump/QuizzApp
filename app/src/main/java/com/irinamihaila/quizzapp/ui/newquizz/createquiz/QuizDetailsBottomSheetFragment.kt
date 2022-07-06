package com.irinamihaila.quizzapp.ui.newquizz.createquiz

import android.content.ContentValues
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
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
import com.irinamihaila.quizzapp.ui.base.BaseActivity
import com.irinamihaila.quizzapp.ui.newquizz.QuizAvailableAdapter
import com.irinamihaila.quizzapp.ui.newquizz.availablequizzes.SeeAvailableQuizActivity
import com.irinamihaila.quizzapp.ui.newquizz.takequiz.QuizViewModel
import com.irinamihaila.quizzapp.ui.newquizz.takequiz.TakeQuizViewModel
import com.irinamihaila.quizzapp.utils.Constants.dateFormatter
import com.irinamihaila.quizzapp.utils.PdfUtils.createPDF
import com.irinamihaila.quizzapp.utils.SharedPrefsUtils
import com.irinamihaila.quizzapp.utils.value
import com.itextpdf.text.Document
import com.itextpdf.text.pdf.PdfWriter
import kotlinx.coroutines.flow.collectLatest

private const val ARG_QUIZ = "QUIZ"
private const val ARG_QUIZ_POS = "QUIZ_POS"
private const val DEFAULT_VALUE = 0

class QuizDetailsBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var quiz: Quiz
    private var uri: Uri? = null
    private var quizPos: Int = DEFAULT_VALUE
    private val viewModel by activityViewModels<QuizViewModel> {
        QuizViewModel.Factory(
            SharedPrefsUtils(requireContext())
        )
    }
    private val leaderboardViewModel by activityViewModels<TakeQuizViewModel>()
    private lateinit var binding: FragmentQuizDetailsBottomSheetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            quizPos = it.getInt(ARG_QUIZ_POS)
            quiz = it.getParcelable(ARG_QUIZ)
                ?: throw IllegalArgumentException("Need a quiz passed as params.")
        }
        leaderboardViewModel.quiz = quiz
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
                        false -> displayError(it.second)
                    }
                }
            }
        }
        leaderboardViewModel.leaderboardLiveData.observe(viewLifecycleOwner) { leaderboard ->
            val leaderboardFormatted = with(StringBuilder()) {
                leaderboard.sortedByDescending { it.second }.forEach {
                    appendLine("${it.first} scored ${it.second}%")
                }
                toString()
            }
            uri?.let { uriSafe ->
                val document = Document()
                try {
                    val outputStream = requireActivity().contentResolver.openOutputStream(uriSafe)
                    PdfWriter.getInstance(document, outputStream)
                    document.open()
                    document.createPDF("Leaderboard ${quiz.name}", leaderboardFormatted)
                    document.close()
                    dismiss()
                    (requireActivity() as BaseActivity).displayInfo("PDF successfully created.")
                } catch (e: Throwable) {
                    Log.e(this::class.java.simpleName, "setupObservers: ${e.localizedMessage}")
                } finally {
                    document.close()
                }
            }
        }
    }

    private fun displayError(message: String?) {
        Snackbar.make(
            binding.root,
            message ?: getText(R.string.generic_error),
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun setupListeners() {
        with(binding) {
            btnUpdateQuiz.setOnClickListener {
                quiz.apply {
                    try {
                        name = etQuizName.value()
                        issuedDate =
                            dateFormatter.parse(etCreationDate.value())
                                ?.let { date -> return@let dateFormatter.format(date) }
                        isRedo = switchIsRedo.isChecked
                        getAdapter().updateItem(quiz, quizPos)
                        viewModel.updateQuiz(quiz)
                    } catch (e: Throwable) {
                        displayError(e.localizedMessage)
                    }
                }
            }
            btnExportQuizAsPDF.setOnClickListener {
                leaderboardViewModel.getLeaderboard(
                    quiz.id ?: throw IllegalStateException("Must have a valid QuizID")
                )
                val values = ContentValues()
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, "Leaderboard ${quiz.name}")
                values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                values.put(
                    MediaStore.MediaColumns.RELATIVE_PATH,
                    Environment.DIRECTORY_DOCUMENTS + "/QuizApp_Leaderboards"
                )
                uri = requireActivity().contentResolver.insert(
                    MediaStore.Files.getContentUri("external"),
                    values
                )
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