package com.irinamihaila.quizzapp.ui.newquizz

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.irinamihaila.quizzapp.R
import com.irinamihaila.quizzapp.databinding.ItemQuizAvailableBinding
import com.irinamihaila.quizzapp.models.Quiz
import com.irinamihaila.quizzapp.ui.newquizz.QuizAvailableAdapter.AvailableQuizVH
import com.irinamihaila.quizzapp.utils.show

class QuizAvailableAdapter(
    private val isAuthorMode: Boolean,
    private val quizList: MutableList<Quiz>,
    private val onQuizClickListener: (quiz: Quiz, isLongPress: Boolean) -> Unit,
    private val onQuizDeleteListener: (quiz: Quiz) -> Unit
) : RecyclerView.Adapter<AvailableQuizVH>() {
    private lateinit var binding: ItemQuizAvailableBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AvailableQuizVH {
        binding =
            ItemQuizAvailableBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return AvailableQuizVH(binding, onQuizClickListener, isAuthorMode, onQuizDeleteListener)
    }

    override fun onBindViewHolder(holder: AvailableQuizVH, position: Int) {
        holder.bind(quizList[position])
    }

    override fun getItemCount(): Int = quizList.size

    class AvailableQuizVH(
        private val binding: ItemQuizAvailableBinding,
        private val onQuizClickListener: (quiz: Quiz, isLongPress: Boolean) -> Unit,
        private val isAuthorMode: Boolean,
        private val onQuizDeleteListener: (quiz: Quiz) -> Unit
    ) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(quiz: Quiz) {
            with(binding) {
                if (isAuthorMode) {
                    btnDeleteQuiz.show()
                    btnDeleteQuiz.setOnClickListener { onQuizDeleteListener(quiz) }
                }
                root.setOnLongClickListener {
                    onQuizClickListener(quiz, true)
                    true
                }
                root.setOnClickListener { onQuizClickListener(quiz, false) }
                ivQuiz.setImageResource(R.drawable.quiz_header_picture)
                tvQuizAvailableTitle.text = quiz.name
                quiz.percentage?.let { tvQuizPercentage.text = "$it%" }
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

    fun addToList(availableQuiz: Quiz) {
        quizList.add(availableQuiz)
        notifyItemInserted(quizList.size)
    }
}
