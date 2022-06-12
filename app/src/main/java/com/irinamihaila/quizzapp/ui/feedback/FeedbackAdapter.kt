package com.irinamihaila.quizzapp.ui.feedback

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.irinamihaila.quizzapp.databinding.ItemFeedbackBinding
import com.irinamihaila.quizzapp.models.Feedback

class FeedbackAdapter(
    private val feedbackList: MutableList<Feedback>,
) : RecyclerView.Adapter<FeedbackAdapter.FeedbackAdapterVH>() {
    private lateinit var binding: ItemFeedbackBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FeedbackAdapterVH {
        binding =
            ItemFeedbackBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return FeedbackAdapterVH(binding)
    }

    override fun onBindViewHolder(holder: FeedbackAdapterVH, position: Int) {
        holder.bind(feedbackList[position], position)
    }

    override fun getItemCount(): Int = feedbackList.size

    inner class FeedbackAdapterVH(
        private val binding: ItemFeedbackBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(feedback: Feedback, position: Int) {
            with(binding) {
                tvFeedback.text = feedback.feedbackMessage
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshList(newFeedback: List<Feedback>) {
        feedbackList.clear()
        feedbackList.addAll(newFeedback)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addToList(newFeedback: Feedback) {
        feedbackList.add(newFeedback)
        notifyItemInserted(feedbackList.size)
    }
}
