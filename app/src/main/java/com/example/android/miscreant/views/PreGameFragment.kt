/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.views

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat.getColor
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.android.miscreant.Enums.Difficulty
import com.example.android.miscreant.Enums.Hero
import com.example.android.miscreant.R
import com.example.android.miscreant.databinding.FragmentPreGameBinding
import java.lang.IllegalArgumentException


class PreGameFragment : Fragment() {

    private var heroType: Hero = Hero.viking

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentPreGameBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_pre_game, container, false)

        // button onClickListeners
        binding.archerButton.setOnClickListener { view: View ->
            heroType = Hero.archer
            view.setBackgroundColor(getColor(context ?: throw IllegalArgumentException("Context NULL"), R.color.backgroundSelected))
            binding.vikingButton.setBackgroundColor(getColor(context ?: throw IllegalArgumentException("Context NULL"), R.color.backgroundBlack))
        }

        binding.vikingButton.setOnClickListener { view: View ->
            heroType = Hero.viking
            view.setBackgroundColor(getColor(context ?: throw IllegalArgumentException("Context NULL"), R.color.backgroundSelected))
            binding.archerButton.setBackgroundColor(getColor(context ?: throw IllegalArgumentException("Context NULL"), R.color.backgroundBlack))
        }

        binding.backButton.setOnClickListener{ view: View ->
            fragmentManager?.popBackStack() ?: Log.e(this.javaClass.simpleName,"${getString(R.string.fragmentManager_null)} ${view.contentDescription}")
        }

        binding.okButton.setOnClickListener{ view: View ->

            val gameDifficulty: Difficulty = when(binding.gameDifficultyGroup.checkedRadioButtonId){
                                                binding.easyRadioButton.id -> Difficulty.easy
                                                binding.normalRadioButton.id -> Difficulty.normal
                                                else -> Difficulty.hard
                                            }

            val heroName = binding.nameEdit.text.ifEmpty{ resources.getString(R.string.default_name) }.toString()

            view.findNavController().navigate(PreGameFragmentDirections.actionPreGameFragmentToGameFragment(gameDifficulty, heroName, heroType))
        }

        return binding.root
    }

}
