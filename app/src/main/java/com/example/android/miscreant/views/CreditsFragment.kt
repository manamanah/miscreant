/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.views

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.android.miscreant.R
import com.example.android.miscreant.databinding.FragmentCreditsBinding


class CreditsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding: FragmentCreditsBinding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_credits, container, false)

        binding.licenseText.movementMethod = LinkMovementMethod.getInstance()
        binding.iconsText.movementMethod = LinkMovementMethod.getInstance()

        // button onClickListeners
        binding.backButton.setOnClickListener { view: View ->
            fragmentManager?.popBackStack() ?: Log.e(
                this.javaClass.simpleName,
                "${getString(R.string.fragmentManager_null)} ${view.contentDescription}"
            )
        }

        return binding.root
    }
}
