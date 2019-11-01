/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.android.miscreant.HighscoreRepository
import com.example.android.miscreant.database.Highscore

class HighscoreViewModel(private val repository: HighscoreRepository) : ViewModel() {

    val easyList: LiveData<List<Highscore>>
        get() = repository.easyHighscores

    val normalList: LiveData<List<Highscore>>
        get() = repository.normalHighscores

    val hardList: LiveData<List<Highscore>>
        get() = repository.hardHighscores


    fun deleteButBest(difficulty: String){
        repository.deleteButBest(difficulty)
    }

    fun deleteList(difficulty: String){
        repository.deleteDifficultyList(difficulty)
    }
}