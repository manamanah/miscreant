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
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import com.example.android.miscreant.R
import com.example.android.miscreant.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        // inflate layout for this fragment
        val binding: FragmentMainBinding = DataBindingUtil.inflate(inflater,
            R.layout.fragment_main, container, false)

        // button onClickListeners
        binding.playButton.setOnClickListener{ view: View ->
            view.findNavController().navigate(MainFragmentDirections.actionMainFragmentToPreGameFragment())
        }

        binding.settingsButton.setOnClickListener{ view: View ->
            view.findNavController().navigate(MainFragmentDirections.actionMainFragmentToSettingsFragment())
        }

        return binding.root
    }
}
