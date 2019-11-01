/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.android.miscreant.database.Highscore
import com.example.android.miscreant.databinding.HighscoreItemBinding
import java.util.*


class HighscoresAdapter : RecyclerView.Adapter<HighscoresAdapter.HighscoreViewHolder>() {

    private var highscores: List<Highscore> = Collections.emptyList()


    override fun getItemCount(): Int = highscores.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HighscoreViewHolder {
        return HighscoreViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: HighscoreViewHolder, position: Int) {
        if (position < itemCount) {
            val item = highscores[position]
            item.position = position + 1 // don't start at 0th position
            holder.bind(item)
        }
    }

    fun updateHighscores(list: List<Highscore>) {
        highscores = list
        notifyDataSetChanged()
    }

    class HighscoreViewHolder(val binding: HighscoreItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(highscoreItem: Highscore) {
            binding.highscore = highscoreItem
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): HighscoreViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = HighscoreItemBinding.inflate(layoutInflater, parent, false)
                return HighscoreViewHolder(binding)
            }
        }
    }
}