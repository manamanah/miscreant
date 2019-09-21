/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.views


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.ViewPager
import com.example.android.miscreant.PageAdapter
import com.example.android.miscreant.R
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.fragment_highscores.*


class HighscoresFragment : Fragment() {

    companion object {
        var nrDifficulties : Int = 5
            private set

        var difficultyNames = mutableListOf<String>()
            private set
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_highscores, container, false)

        nrDifficulties = resources.getInteger(R.integer.game_difficulties)
        difficultyNames.add(resources.getString(R.string.easy))
        difficultyNames.add(resources.getString(R.string.normal))
        difficultyNames.add(resources.getString(R.string.hard))

        val tabAdapter = PageAdapter(childFragmentManager)

        val viewPager = view.findViewById<ViewPager>(R.id.viewPager)
        viewPager.adapter = tabAdapter
        val tabLayout = view.findViewById<TabLayout>(R.id.tab_layout)
        tabLayout.setupWithViewPager(viewPager)

        return view
    }
}
