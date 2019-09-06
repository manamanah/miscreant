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
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import com.example.android.miscreant.Difficulty
import com.example.android.miscreant.Hero
import com.example.android.miscreant.R
import com.example.android.miscreant.viewmodels.GameViewModel
import com.example.android.miscreant.databinding.FragmentGameBinding
import com.example.android.miscreant.viewmodels.GameViewModelFactory
import kotlinx.android.synthetic.main.fragment_game.*
import kotlinx.android.synthetic.main.fragment_game.view.*
import kotlin.math.absoluteValue

class GameFragment : Fragment() {

    private lateinit var gameViewModel: GameViewModel
    private lateinit var gameViewModelFactory: GameViewModelFactory

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentGameBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_game, container, false)

        // allows data binding to observe LiveData in fragment lifecycle
        binding.lifecycleOwner = this

        // viewModelFactory & viewModel
        gameViewModelFactory = GameViewModelFactory(context)
        gameViewModel = ViewModelProviders.of(this, gameViewModelFactory).get(GameViewModel::class.java)
        binding.gameViewModel = gameViewModel

        // todo get navigation arguments from selection in pre-game-fragment
        gameViewModel.initializeGameSettings(Difficulty.easy, "hero", Hero.viking)

        // don't allow user interaction with game progress seekbar
        // custom seekbar takes care of correct look
        binding.seekbarGameProgress.isEnabled = false

        gameViewModel.currentDeckNumber.observe( this, Observer{
            if (it != null){
                binding.seekbarGameProgress.progress = it
            }
        })

        // menu buttons setOnClickListener
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
