/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant

import android.content.Context
import android.os.AsyncTask
import androidx.lifecycle.LiveData
import com.example.android.miscreant.Enums.Difficulty
import com.example.android.miscreant.database.Highscore
import com.example.android.miscreant.database.HighscoreDatabase
import com.example.android.miscreant.database.HighscoreDatabaseDao


class HighscoreRepository(context: Context) {

    private var highscoreDatabaseDao: HighscoreDatabaseDao =
        HighscoreDatabase.getInstance(context).highscoreDatabaseDao

    private var easyHighscores: LiveData<List<Highscore>>
    private var normalHighscores: LiveData<List<Highscore>>
    private var hardHighscores: LiveData<List<Highscore>>

    private var insertAsyncTask: AsyncTask<Highscore, Unit, Unit>? = null
    private var deleteAsyncTask: AsyncTask<String, Unit, Unit>? = null
    private var deleteButBestAsyncTask: AsyncTask<String, Unit, Unit>? = null

    init {
        easyHighscores = highscoreDatabaseDao.getDifficultyHighscores(Difficulty.easy.title)
        normalHighscores = highscoreDatabaseDao.getDifficultyHighscores(Difficulty.normal.title)
        hardHighscores = highscoreDatabaseDao.getDifficultyHighscores(Difficulty.hard.title)
    }

    fun insert(highscore: Highscore) {
        insertAsyncTask = InsertAsyncTask(highscoreDatabaseDao).execute(highscore)
    }

    fun deleteDifficultyList(difficulty: String) {
        deleteAsyncTask = DeleteDifficultyList(highscoreDatabaseDao).execute(difficulty)
    }

    fun deleteButBest(difficulty: String) {
        deleteButBestAsyncTask = DeleteButBestList(highscoreDatabaseDao).execute(difficulty)
    }

    fun cancel() {
        if (insertAsyncTask?.status != AsyncTask.Status.FINISHED) {
            insertAsyncTask?.cancel(true)
        }

        if (deleteAsyncTask?.status != AsyncTask.Status.FINISHED) {
            deleteAsyncTask?.cancel(true)
        }

        if (deleteButBestAsyncTask?.status != AsyncTask.Status.FINISHED) {
            deleteButBestAsyncTask?.cancel(true)
        }
    }

    fun getDifficultyList(name: String): LiveData<List<Highscore>> {
        return when (name) {
            Difficulty.easy.title -> easyHighscores
            Difficulty.normal.title -> normalHighscores
            else -> hardHighscores
        }
    }

    private class InsertAsyncTask(val highscoreDao: HighscoreDatabaseDao) :
        AsyncTask<Highscore, Unit, Unit>() {

        override fun doInBackground(vararg params: Highscore?) {
            params[0]?.let {
                highscoreDao.insert(it)
            }
        }
    }

    private class DeleteDifficultyList(val highscoreDao: HighscoreDatabaseDao) :
        AsyncTask<String, Unit, Unit>() {

        override fun doInBackground(vararg params: String?) {
            params[0]?.let {
                highscoreDao.clearDifficulty(it)
            }
        }
    }

    private class DeleteButBestList(val highscoreDao: HighscoreDatabaseDao) :
        AsyncTask<String, Unit, Unit>() {

        override fun doInBackground(vararg params: String?) {
            params[0]?.let {
                highscoreDao.clearButBest(it)
            }
        }
    }
}