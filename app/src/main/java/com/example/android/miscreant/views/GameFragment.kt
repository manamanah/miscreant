/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.views

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import androidx.core.view.isVisible
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.android.miscreant.*
import com.example.android.miscreant.viewmodels.GameViewModel
import com.example.android.miscreant.databinding.FragmentGameBinding
import com.example.android.miscreant.viewmodels.GameViewModelFactory


class GameFragment : Fragment(), View.OnTouchListener, GestureDetector.OnDoubleTapListener {

    private val gameArguments: GameFragmentArgs by navArgs()

    private lateinit var gameViewModel: GameViewModel
    private lateinit var gameViewModelFactory: GameViewModelFactory
    private lateinit var gestureDetector : GestureDetector
    private lateinit var selectedView : View

    // static access for setListener needed in bindingadapter when creating cards in Fragment
    companion object Listener  {

        private lateinit var gameFragment: GameFragment

        fun setFragmentInstance(gameFragment: GameFragment){
            this.gameFragment = gameFragment
        }

        fun setListeners(view: View){
            view.setOnTouchListener(gameFragment)
        }

        fun onClawEnd(view: CardView){
            gameFragment.onClawEnd(view)
        }

        fun onCounterAttackAnimationEnd(view: CardView){
            gameFragment.onCounterAttackAnimationEnd(view)
        }
    }

    // region touch interactions
    override fun onTouch(view: View?, motionEvent: MotionEvent?): Boolean {
        if (!gameViewModel.counterAttackRunning){

            // don't bother if view not valid
            selectedView = view ?: return true
            gestureDetector.onTouchEvent(motionEvent)
            view.performClick()
        }
        return true
    }

    override fun onDoubleTap(motionEvent: MotionEvent?): Boolean {
        gameViewModel.doubleTap(selectedView)
        return true
    }

    override fun onDoubleTapEvent(motionEvent: MotionEvent?): Boolean {
        return true
    }

    override fun onSingleTapConfirmed(motionEvent: MotionEvent?): Boolean {
        gameViewModel.singleTap(selectedView)
        return true
    }
    // endregion

    fun onClawEnd(view: CardView){
        gameViewModel.onClawEnd(view)
    }

    fun onCounterAttackAnimationEnd(view: CardView) {
        gameViewModel.counterAttackAnimationEnded(view)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val binding: FragmentGameBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_game, container, false)

        // allows data binding to observe LiveData in fragment lifecycle
        binding.lifecycleOwner = this

        // viewModelFactory & viewModel
        gameViewModelFactory = GameViewModelFactory(context)
        gameViewModel = ViewModelProviders.of(this, gameViewModelFactory).get(GameViewModel::class.java)
        binding.gameViewModel = gameViewModel

        // region observers for navigation
        gameViewModel.navigateToLoseFragment.observe(this, Observer {
            if (it != null && it){
                findNavController().navigate(GameFragmentDirections.actionGameFragmentToLoseFragment(
                    gameArguments.gameDifficulty,
                    gameArguments.heroType,
                    gameArguments.heroName,
                    gameViewModel.heroMaxHealth.value ?: 0,
                    gameViewModel.currentDeckNumber.value ?: 0))

                gameViewModel.navigatedToLoseFragment()
            }
        })

        gameViewModel.navigateToWinFragment.observe(this, Observer {
            if (it != null && it){
                findNavController().navigate(GameFragmentDirections.actionGameFragmentToWinFragment(
                    gameArguments.gameDifficulty,
                    gameArguments.heroType,
                    gameArguments.heroName,
                    gameViewModel.heroMaxHealth.value ?: 0,
                    gameViewModel.currentDeckNumber.value ?: 0,
                    gameViewModel.leftItemsValue))

                gameViewModel.navigatedToWinFragment()
            }
        })
        // endregion

        // set up listeners for card touch interaction
        setFragmentInstance(this)
        gestureDetector = GestureDetector(context, GestureDetector.SimpleOnGestureListener())
        gestureDetector.setOnDoubleTapListener(this)
        selectedView = View(context)

        // region init game settings and hero stuff
        gameViewModel.initializeGameSettings(gameArguments.gameDifficulty, gameArguments.heroName, gameArguments.heroType)
        binding.heroSpecial.text = gameViewModel.heroSpecial

        gameViewModel.specialsUsed.observe(this, Observer{
            if (it != null){
                when(it){
                    0 -> {
                        binding.specialUse1.isVisible = true
                        binding.specialUse2.isVisible = true
                        binding.specialUse3.isVisible = true
                    }
                    1 -> binding.specialUse3.isVisible = false
                    2 -> binding.specialUse2.isVisible = false
                    else -> {
                        binding.specialUse1.isVisible = false
                        binding.specialUse2.isVisible = false
                        binding.specialUse3.isVisible = false
                    }
                }
            }
        })
        // endregion

        // region game progress in seekbar
        // don't allow user interaction with game progress seekbar
        // custom seekbar takes care of correct look
        binding.seekbarGameProgress.isEnabled = false

        gameViewModel.currentDeckNumber.observe( this, Observer{
            if (it != null){
                binding.seekbarGameProgress.progress = it
            }
        })
        // endregion

        // region buttons
        binding.backButton.setOnClickListener { view: View ->
            fragmentManager?.popBackStack() ?: Log.e(this.javaClass.simpleName,"${getString(R.string.fragmentManager_null)} ${view.contentDescription}")
        }

        binding.settingsButton.setOnClickListener { view: View ->
            view.findNavController().navigate(GameFragmentDirections.actionGameFragmentToSettingsFragment())
        }

        binding.dealNew.setOnClickListener {
            gameViewModel.dealNewCards()
        }
        // endregion

        binding.gameBackground.setOnTouchListener(this)

        startGame()

        return binding.root
    }

    private fun startGame(){
        gameViewModel.startGame()
    }
}
