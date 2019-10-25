/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.miscreant.Enums.Hero
import com.example.android.miscreant.R
import com.example.android.miscreant.databinding.FragmentLoseBinding


class LoseFragment : Fragment() {

    private val result: LoseFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentLoseBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_lose, container, false)

        binding.okButton.setOnClickListener {
            findNavController().navigate(LoseFragmentDirections.actionLoseFragmentToPreGameFragment())
        }

        // set correct dead hero image
        binding.deadHeroImg.setImageResource(if (result.heroType == Hero.viking) R.drawable.viking_dead else R.drawable.archer_dead)

        binding.heroName.text = result.heroName

        val deckPoints =
            result.accomplishedDecks.times(resources.getInteger(R.integer.deckMultiplier))
        binding.dungeonsAccomplished.text = result.accomplishedDecks.toString()
        binding.dungeonPoints.text = deckPoints.toString()

        val maxHealthPoints =
            result.maxHealth.times(resources.getInteger(R.integer.maxHealthMultiplier))
        binding.maxHealth.text = result.maxHealth.toString()
        binding.maxHealthPoints.text = maxHealthPoints.toString()

        binding.finalScore.text = (deckPoints + maxHealthPoints).toString()

        return binding.root
    }
}
