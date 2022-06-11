package com.irinamihaila.quizzapp.ui.newquizz

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.irinamihaila.quizzapp.databinding.LayoutQuestionItemBinding
import com.irinamihaila.quizzapp.models.Question
import com.irinamihaila.quizzapp.ui.newquizz.QuizItemAdapter.CreateQuizVH
import com.irinamihaila.quizzapp.utils.show
import com.irinamihaila.quizzapp.utils.toEditable
import okhttp3.internal.toImmutableList

class QuizItemAdapter(
    private val questionList: MutableList<Question>,
    private val isCreateMode: Boolean = true,
    val onQuestionClick: (question: Question, position: Int) -> Unit
) : RecyclerView.Adapter<CreateQuizVH>() {
    private lateinit var binding: LayoutQuestionItemBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CreateQuizVH {
        binding =
            LayoutQuestionItemBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return CreateQuizVH(binding, isCreateMode, onQuestionClick)
    }

    override fun onBindViewHolder(holder: CreateQuizVH, position: Int) {
        holder.bind(questionList[position], position)
    }

    override fun getItemCount(): Int = questionList.size

    class CreateQuizVH(
        private val binding: LayoutQuestionItemBinding,
        private val isCreateMode: Boolean,
        private val onQuestionClick: (question: Question, position: Int) -> Unit,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(question: Question, position: Int) {
            with(binding) {
                makeFieldsViewOnly()
                if (isCreateMode) {
                    btnEdit.show()
                    btnEdit.setOnClickListener { onQuestionClick(question, position) }
                }
                with(question) {
                    etNewQuestion.text = this.question?.toEditable()
                    etAnswer1.text = answer1?.toEditable()
                    etAnswer2.text = answer2?.toEditable()
                    etAnswer3.text = answer3?.toEditable()
                    etAnswer4.text = answer4?.toEditable()
                    rbGroup.check(getCorrectAnswer(question))
                }
            }
        }

        private fun makeFieldsViewOnly() {
            with(binding) {
                for (child in rbGroup.children) {
                    child.isEnabled = false
                }
                etNewQuestion.isEnabled = false
                etAnswer1.isEnabled = false
                etAnswer2.isEnabled = false
                etAnswer3.isEnabled = false
                etAnswer4.isEnabled = false
            }
        }

        private fun getCorrectAnswer(question: Question) =
            with(binding) {
                when (question.correctAnswer) {
                    question.answer1 -> rbAnswer1.id
                    question.answer2 -> rbAnswer2.id
                    question.answer3 -> rbAnswer3.id
                    question.answer4 -> rbAnswer4.id
                    else -> throw IllegalStateException("There can only be 4 types of answers!")
                }
            }
    }

    fun addItem(question: Question) {
        questionList.add(question)
        notifyItemInserted(questionList.lastIndex)
    }

    fun updateQuestion(oldQuestionPos: Int, newQuestion: Question) {
        questionList[oldQuestionPos] = newQuestion
        notifyItemChanged(oldQuestionPos)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshList(questions: List<Question>) {
        questionList.clear()
        questionList.addAll(questions)
        notifyDataSetChanged()
    }

    fun getQuestions(): List<Question> = questionList.toImmutableList()
}
