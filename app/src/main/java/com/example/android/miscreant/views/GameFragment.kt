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
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.android.miscreant.R
import com.example.android.miscreant.viewmodels.GameViewModel
import com.example.android.miscreant.databinding.FragmentGameBinding
import com.example.android.miscreant.viewmodels.GameViewModelFactory

class GameFragment : Fragment() {

    private lateinit var gameViewModel: GameViewModel
    private lateinit var gameViewModelFactory: GameViewModelFactory

    private var currentDeck = 1
    private var gameOver = false

    // todo: fragment_game seekbar: on touch event just return false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentGameBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_game, container, false)

        // viewModelFactory & viewModel
        gameViewModelFactory = GameViewModelFactory(context)
        gameViewModel = ViewModelProviders.of(this, gameViewModelFactory).get(GameViewModel::class.java)
        binding.gameViewModel = gameViewModel

        // button setOnClickListener
        binding.backButton.setOnClickListener { view: View ->
            fragmentManager?.popBackStack() ?: Log.e(this.javaClass.simpleName,"${getString(R.string.fragmentManager_null)} ${view.contentDescription}")
        }

        binding.settingsButton.setOnClickListener { view: View ->
            view.findNavController().navigate(GameFragmentDirections.actionGameFragmentToSettingsFragment())
        }

        // todo deliver infos on game difficulty, hero name, hero type onStartGame
        // start game
        startGame()

        return binding.root
    }

    private fun startGame(){
        gameViewModel.startGame()
    }
}
