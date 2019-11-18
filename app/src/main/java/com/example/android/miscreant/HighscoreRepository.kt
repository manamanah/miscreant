/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant

import android.app.Application
import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.android.miscreant.Enums.Difficulty
import com.example.android.miscreant.database.Highscore
import com.example.android.miscreant.database.HighscoreDatabase
import com.example.android.miscreant.database.HighscoreDatabaseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


class HighscoreRepository(application: Application) {

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    private var highscoreDatabaseDao: HighscoreDatabaseDao =
        HighscoreDatabase.getInstance(application.baseContext).highscoreDatabaseDao

    private val _easyHighscores = MutableLiveData<List<Highscore>>().apply {
        postValue(listOf())
    }
    val easyHighscores: LiveData<List<Highscore>>
        get()= _easyHighscores

    private val _normalHighscores = MutableLiveData<List<Highscore>>().apply {
        postValue(listOf())
    }
    val normalHighscores: LiveData<List<Highscore>>
        get()= _normalHighscores

    private val _hardHighscores = MutableLiveData<List<Highscore>>().apply {
        postValue(listOf())
    }
    val hardHighscores: LiveData<List<Highscore>>
        get() = _hardHighscores

    init {
        coroutineScope.launch {
            _easyHighscores.value = highscoreDatabaseDao.getDifficultyHighscores(Difficulty.easy.title)
            _normalHighscores.value = highscoreDatabaseDao.getDifficultyHighscores(Difficulty.normal.title)
            _hardHighscores.value = highscoreDatabaseDao.getDifficultyHighscores(Difficulty.hard.title)
        }
    }


    fun insert(highscore: Highscore, difficulty: String) {
        coroutineScope.launch {
            highscoreDatabaseDao.insert(highscore)
            updateList(difficulty)
        }
    }

    fun deleteDifficultyList(difficulty: String) {
        coroutineScope.launch {
            highscoreDatabaseDao.clearDifficulty(difficulty)
            updateList(difficulty)
        }
    }

    fun deleteButBest(difficulty: String) {
        coroutineScope.launch {
            highscoreDatabaseDao.clearButBest(difficulty)
            updateList(difficulty)
        }
    }


    private suspend fun updateList(difficulty: String){
        val result = highscoreDatabaseDao.getDifficultyHighscores(difficulty)

        when (difficulty) {
            Difficulty.easy.title -> _easyHighscores.postValue(result)
            Difficulty.normal.title -> _normalHighscores.postValue(result)
            Difficulty.hard.title -> _hardHighscores.postValue(result)
        }
    }

    fun cancel() {
        job.cancel()
    }
}