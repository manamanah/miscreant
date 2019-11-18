/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.views


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.example.android.miscreant.Enums.Difficulty
import com.example.android.miscreant.HighscoreRepository
import com.example.android.miscreant.PageAdapter
import com.example.android.miscreant.R
import com.example.android.miscreant.viewmodels.HighscoreViewModel
import com.example.android.miscreant.viewmodels.HighscoreViewModelFactory
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_highscores.*


class HighscoresFragment : Fragment() {

    companion object {
        var nrDifficulties: Int = 5
            private set

        var difficultyNames = mutableListOf<String>()
            private set
    }

    private lateinit var repository: HighscoreRepository
    private lateinit var highscoreViewModel: HighscoreViewModel


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_highscores, container, false)

        nrDifficulties = resources.getInteger(R.integer.game_difficulties)
        difficultyNames.add(resources.getString(R.string.easy))
        difficultyNames.add(resources.getString(R.string.normal))
        difficultyNames.add(resources.getString(R.string.hard))

        repository =
            HighscoreRepository(activity?.application ?: throw IllegalArgumentException("ACTIVITY is null"))

        // viewModelFactory & viewModel
        val highscoreViewModelFactory = HighscoreViewModelFactory(repository)
        highscoreViewModel =
            ViewModelProviders.of(this, highscoreViewModelFactory).get(HighscoreViewModel::class.java)


        // button onClickListeners
        val backButton = view.findViewById<ImageView>(R.id.back_button)
        backButton.setOnClickListener {
            fragmentManager?.popBackStack() ?: Log.e(
                this.javaClass.simpleName,
                "${getString(R.string.fragmentManager_null)} ${it.contentDescription}"
            )
        }

        // button listeners
        val keepButton = view.findViewById<Button>(R.id.keep_best_button)
        keepButton.setOnClickListener {
            when (viewPager.currentItem) {
                0 -> highscoreViewModel.deleteButBest(Difficulty.easy.title)
                1 -> highscoreViewModel.deleteButBest(Difficulty.normal.title)
                else -> highscoreViewModel.deleteButBest(Difficulty.hard.title)
            }
        }

        val deleteButton = view.findViewById<Button>(R.id.delete_all_button)
        deleteButton.setOnClickListener {
            when (viewPager.currentItem) {
                0 -> highscoreViewModel.deleteList(Difficulty.easy.title)
                1 -> highscoreViewModel.deleteList(Difficulty.normal.title)
                else -> highscoreViewModel.deleteList(Difficulty.hard.title)
            }
        }

        val tabAdapter = PageAdapter(childFragmentManager, highscoreViewModel)

        val viewPager = view.findViewById<ViewPager>(R.id.viewPager)
        viewPager.adapter = tabAdapter
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)

        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        repository.cancel()
    }
}
