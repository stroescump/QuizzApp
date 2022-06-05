package com.irinamihaila.quizzapp.ui.leaderboard

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.irinamihaila.quizzapp.databinding.ItemLeaderboardBinding
import com.irinamihaila.quizzapp.ui.leaderboard.LeaderboardItemAdapter.LeaderboardItemVH

class LeaderboardItemAdapter(
    private val playerList: MutableList<Pair<String, Int>>,
) : RecyclerView.Adapter<LeaderboardItemVH>() {
    private lateinit var binding: ItemLeaderboardBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LeaderboardItemVH {
        binding =
            ItemLeaderboardBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        return LeaderboardItemVH(binding)
    }

    override fun onBindViewHolder(holder: LeaderboardItemVH, position: Int) {
        holder.bind(playerList[position], position)
    }

    override fun getItemCount(): Int = playerList.size

    class LeaderboardItemVH(
        private val binding: ItemLeaderboardBinding,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(playerDetails: Pair<String, Int>, position: Int) {
            with(binding) {
                tvPlayerDetails.text = "${position.inc()}. ${playerDetails.first} ${playerDetails.second}%"
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun refreshList(newPlayers: List<Pair<String, Int>>) {
        playerList.clear()
        playerList.addAll(newPlayers)
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addToList(newPlayer: Pair<String, Int>) {
        playerList.add(newPlayer)
        notifyItemInserted(playerList.size)
    }
}
