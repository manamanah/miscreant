/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.android.miscreant.HighscoresAdapter
import com.example.android.miscreant.R
import com.example.android.miscreant.viewmodels.HighscoreViewModel


class HighscoreEasyFragment(private val viewModel: HighscoreViewModel) : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_highscore_easy, container, false)

        viewManager = LinearLayoutManager(context)
        viewAdapter = HighscoresAdapter()

        viewModel.easyList.observe(this, Observer {list ->
            val tempAdapter = viewAdapter as HighscoresAdapter
            tempAdapter.updateHighscores(list)
        })

        recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView).apply {
            layoutManager = viewManager
            adapter = viewAdapter
        }
        return view
    }

    override fun onDestroyView() {
        viewModel.easyList.removeObservers(this)
        super.onDestroyView()
    }
}
