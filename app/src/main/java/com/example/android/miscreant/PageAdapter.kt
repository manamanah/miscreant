/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.example.android.miscreant.views.HighscoreEasyFragment
import com.example.android.miscreant.views.HighscoreHardFragment
import com.example.android.miscreant.views.HighscoreNormalFragment
import com.example.android.miscreant.views.HighscoresFragment


class PageAdapter(fragmentManager: FragmentManager) :
    FragmentStatePagerAdapter(fragmentManager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

    override fun getItem(position: Int): Fragment {
        return when (position) {
            0 -> HighscoreEasyFragment()
            1 -> HighscoreNormalFragment()
            else -> HighscoreHardFragment()
        }
    }

    // number of tabs
    override fun getCount(): Int = HighscoresFragment.nrDifficulties

    override fun getPageTitle(position: Int): CharSequence? {
        if (position < HighscoresFragment.difficultyNames.size) {
            return HighscoresFragment.difficultyNames[position]
        }
        Log.e(
            this.javaClass.simpleName,
            "Position higher $position than size of difficultyNames ${HighscoresFragment.difficultyNames.size}"
        )
        return "TITLE"
    }
}