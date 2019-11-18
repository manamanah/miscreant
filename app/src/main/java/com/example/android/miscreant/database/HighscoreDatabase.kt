/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase


@Database(entities = [Highscore::class], version = 2, exportSchema = false)
abstract class HighscoreDatabase : RoomDatabase() {

    abstract val highscoreDatabaseDao: HighscoreDatabaseDao

    companion object {

        const val tableName = "highscores_table"
        const val difficulty: String = "difficulty"
        const val heroType: String = "heroType"
        const val heroName: String = "heroName"
        const val points: String = "points"
        const val position: String = "position"

        @Volatile
        private var INSTANCE: HighscoreDatabase? = null

        fun getInstance(context: Context): HighscoreDatabase {
            synchronized(this) {
                var instance = INSTANCE

                if (instance == null) {
                    instance = Room.databaseBuilder(
                                    context.applicationContext,
                                    HighscoreDatabase::class.java,
                                    tableName
                                )
                                    // 1st version no migrations yet
                                    .addMigrations(InitialMigration())
                                    .build()

                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}