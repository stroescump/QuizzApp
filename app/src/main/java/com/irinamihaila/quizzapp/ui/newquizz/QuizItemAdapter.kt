package com.irinamihaila.quizzapp.ui.newquizz

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.irinamihaila.quizzapp.databinding.FragmentCreateQuizBottomSheetItemBinding
import com.irinamihaila.quizzapp.models.Question
import com.irinamihaila.quizzapp.ui.newquizz.QuizItemAdapter.CreateQuizVH
import com.irinamihaila.quizzapp.utils.toEditable

class QuizItemAdapter(
    private val questionList: MutableList<Question>,
    private val isCreateMode: Boolean = true
) : RecyclerView.Adapter<CreateQuizVH>() {
    private lateinit var binding: FragmentCreateQuizBottomSheetItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateQuizVH {
        binding =
            FragmentCreateQuizBottomSheetItemBinding.inflate(LayoutInflater.from(parent.context))
        return CreateQuizVH(binding, isCreateMode)
    }

    override fun onBindViewHolder(holder: CreateQuizVH, position: Int) {
        holder.bind(questionList[position])
    }

    override fun getItemCount(): Int = questionList.size

    class CreateQuizVH(
        private val binding: FragmentCreateQuizBottomSheetItemBinding,
        val isCreateMode: Boolean
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(question: Question) {
            with(binding) {
                if (isCreateMode) {
                    makeFieldsViewOnly()
                }
                with(question) {
                    etNewQuestion.text = this.question.toEditable()
                    etAnswer1.text = answer1.toEditable()
                    etAnswer2.text = answer2.toEditable()
                    etAnswer3.text = answer3.toEditable()
                    etAnswer4.text = answer4.toEditable()
                }
            }
        }

        private fun makeFieldsViewOnly() {
            with(binding) {
                rbGroup.isEnabled = false
                etNewQuestion.isEnabled = false
                etAnswer1.isEnabled = false
                etAnswer2.isEnabled = false
                etAnswer3.isEnabled = false
                etAnswer4.isEnabled = false
            }
        }
    }

    fun addItem(question: Question) {
        questionList.add(question)
        notifyItemInserted(questionList.lastIndex)
    }
}
