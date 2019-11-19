/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.android.miscreant.Repository
import com.example.android.miscreant.database.Highscore

class HighscoreViewModel(private val repository: Repository) : ViewModel() {

    private val _easyList: LiveData<List<Highscore>> = repository.easyHighscores
    val easyList: LiveData<List<Highscore>>
        get() = _easyList

    private val _normalList: LiveData<List<Highscore>> = repository.normalHighscores
    val normalList: LiveData<List<Highscore>>
        get() = _normalList

    private val _hardList: LiveData<List<Highscore>> = repository.hardHighscores
    val hardList: LiveData<List<Highscore>>
        get() = _hardList


    fun deleteButBest(difficulty: String){
        repository.deleteButBest(difficulty)
    }

    fun deleteList(difficulty: String){
        repository.deleteDifficultyList(difficulty)
    }
}