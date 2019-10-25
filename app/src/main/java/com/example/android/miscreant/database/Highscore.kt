/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.android.miscreant.Enums.Difficulty
import com.example.android.miscreant.Enums.Hero


@Entity(tableName = HighscoreDatabase.tableName)
data class Highscore(
    @PrimaryKey(autoGenerate = true)
    var highscoreId: Long = 0L,

    @ColumnInfo(name = HighscoreDatabase.heroType)
    val heroType: String = Hero.viking.imageName,

    @ColumnInfo(name = HighscoreDatabase.difficulty)
    val difficulty: String = Difficulty.easy.title,

    @ColumnInfo(name = HighscoreDatabase.heroName)
    val heroName: String = "Hero",

    @ColumnInfo(name = HighscoreDatabase.points)
    val points: Int = -1,

    @ColumnInfo(name = HighscoreDatabase.position)
    var position: Int = -1
)