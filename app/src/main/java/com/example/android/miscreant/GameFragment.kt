/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant


import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.android.miscreant.databinding.FragmentGameBinding

class GameFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val binding: FragmentGameBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false)

        // button setOnClickListener
        binding.backButton.setOnClickListener { view: View ->
            fragmentManager?.popBackStack() ?: Log.e(this.javaClass.simpleName,"${getString(R.string.fragmentManager_null)} ${view.contentDescription}")
        }

        binding.settingsButton.setOnClickListener { view: View ->
            view.findNavController().navigate(GameFragmentDirections.actionGameFragmentToSettingsFragment())
        }

        return binding.root
    }

}