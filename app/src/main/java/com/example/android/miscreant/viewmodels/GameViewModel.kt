/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.android.miscreant.models.Card
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.IllegalArgumentException

class GameViewModel(context: Context?) : ViewModel() {

    private val context: Context = context ?: throw IllegalArgumentException("${this.javaClass.simpleName}: context is NULL" )
    private val testdeckPath = "testdeck.json"

    private var testDeck: MutableList<Card>

    init {
        testDeck = loadDeck(testdeckPath)
    }

    // todo later 3 decks per playthrough
    // better throw exception -> no game possible w/o deck
    private fun loadDeck(filePath : String) : MutableList<Card> {

        val bufferedReader = context.assets.open(filePath).bufferedReader()
        val jsonString = bufferedReader.use{ it.readText() } //read and store in string

        val type = object : TypeToken<MutableList<Card>>(){}.type
        return Gson().fromJson(jsonString, type)
    }
}