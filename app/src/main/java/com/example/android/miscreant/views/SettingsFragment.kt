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
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.example.android.miscreant.R
import com.example.android.miscreant.databinding.FragmentSettingsBinding
import java.util.*


class SettingsFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val binding: FragmentSettingsBinding =
            DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_settings, container, false
            )

        // button onClickListeners
        binding.okButton.setOnClickListener { view: View ->

            val configuration = resources.configuration

            // simply returns first two letters of language, e.g. "en"
            val selectedLanguage = when (binding.languageGroup.checkedRadioButtonId) {
                binding.germanRadioButton.id -> resources.getString(R.string.language_de)
                else -> resources.getString(R.string.language_en)
            }

            @Suppress("DEPRECATION")
            val currentLocale =
                if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.N) resources.configuration.locale
                else resources.configuration.locales.get(0)

            if (!currentLocale.language.startsWith(selectedLanguage)) {
                configuration.setLocale(Locale(selectedLanguage))

                @Suppress("DEPRECATION")
                resources.updateConfiguration(configuration, resources.displayMetrics)
            }
            fragmentManager?.popBackStack() ?: Log.e(
                this.javaClass.simpleName,
                "${getString(R.string.fragmentManager_null)} ${(view as Button).text}"
            )
        }

        return binding.root
    }
}