/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface HighscoreDatabaseDao {

    // at gameEnd (win or lose) inserted - there are no updates
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(highscore: Highscore)

    @Query("SELECT * from highscores_table WHERE difficulty = :difficulty ORDER BY points DESC")
    fun getDifficultyHighscores(difficulty: String): LiveData<List<Highscore>>

    @Query("DELETE FROM highscores_table WHERE difficulty =:difficulty")
    fun clearDifficulty(difficulty: String)

    @Query("DELETE FROM highscores_table WHERE highscoreId NOT IN (SELECT highscoreId FROM highscores_table WHERE difficulty =:difficulty ORDER BY points DESC LIMIT 15)")
    fun clearButBest(difficulty: String)
}