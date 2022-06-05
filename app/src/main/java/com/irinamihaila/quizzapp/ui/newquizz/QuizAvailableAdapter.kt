package com.irinamihaila.quizzapp.ui.newquizz

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.irinamihaila.quizzapp.databinding.ItemQuizAvailableBinding
import com.irinamihaila.quizzapp.models.Quiz
import com.irinamihaila.quizzapp.ui.newquizz.QuizAvailableAdapter.AvailableQuizVH

class QuizAvailableAdapter(
    private val quizList: MutableList<Quiz>,
    private val onQuizClickListener: (quiz: Quiz) -> Unit
) : RecyclerView.Adapter<AvailableQuizVH>() {
    private lateinit var binding: ItemQuizAvailableBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableQuizVH {
        binding =
            ItemQuizAvailableBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return AvailableQuizVH(binding, onQuizClickListener)
    }

    override fun onBindViewHolder(holder: AvailableQuizVH, position: Int) {
        holder.bind(quizList[position])
    }

    override fun getItemCount(): Int = quizList.size

    class AvailableQuizVH(
        private val binding: ItemQuizAvailableBinding,
        private val onQuizClickListener: (quiz: Quiz) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(quiz: Quiz) {
            with(binding) {
                root.setOnClickListener { onQuizClickListener(quiz) }
                tvQuizAvailableTitle.text = quiz.name
                tvQuizAvailableIsRedoEnabled.text = "Redo available: ${quiz.isRedo}"
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshList(availableQuizList: List<Quiz>) {
        quizList.clear()
        quizList.addAll(availableQuizList)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addToList(availableQuiz: Quiz) {
        quizList.add(availableQuiz)
        notifyItemInserted(quizList.size)
    }
}