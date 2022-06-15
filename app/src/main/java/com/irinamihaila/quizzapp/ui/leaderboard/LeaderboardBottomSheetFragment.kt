package com.irinamihaila.quizzapp.ui.leaderboard

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.snackbar.Snackbar
import com.irinamihaila.quizzapp.R
import com.irinamihaila.quizzapp.databinding.FragmentLeaderboardBottomSheetBinding
import com.irinamihaila.quizzapp.ui.newquizz.TakeQuizViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LeaderboardBottomSheetFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentLeaderboardBottomSheetBinding
    private val viewModel: TakeQuizViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLeaderboardBottomSheetBinding.inflate(layoutInflater, null, false)
        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
        setupObservers()
        lifecycleScope.launch(Dispatchers.IO) {
            viewModel.quiz.id?.let { viewModel.getLeaderboard(it) }
        }
        with(binding) {
            rvLeaderboard.adapter = LeaderboardItemAdapter(mutableListOf())
            val percentage =
                getPercentage()
            tvPercentage.text = "$percentage%\nSee how your \ncolleagues did"
        }
    }

    private fun setupListeners() {
        binding.btnCloseSheet.setOnClickListener { dismiss() }
    }

    private fun setupObservers() {
        viewModel.leaderboardLiveData.observe(viewLifecycleOwner) { leaderboard ->
            val updatedList = leaderboard.toMutableList().also {
                it.add("You" to getPercentage())
            }
            (binding.rvLeaderboard.adapter as LeaderboardItemAdapter).refreshList(updatedList.sortedByDescending { it.second })
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            displayError(it.localizedMessage)
        }
    }

    private fun getPercentage() =
        viewModel.quiz.questions?.size?.let {
            return@let viewModel.correctAnswers.toFloat().div(it.toFloat()).times(100f).toInt()
        } ?: throw IllegalArgumentException("Percentage cannot be null.")


    private fun displayError(message: String? = null) {
        createSnackbar(binding.root, message ?: getString(R.string.generic_error))
    }

    private fun createSnackbar(view: View, message: String) =
        Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show()

    companion object {
        @JvmStatic
        fun newInstance() =
            LeaderboardBottomSheetFragment()
    }
}