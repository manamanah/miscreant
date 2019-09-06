/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.models

import com.example.android.miscreant.Difficulty
import com.example.android.miscreant.Hero

data class Settings(val difficulty: Difficulty = Difficulty.easy, val heroName: String = "", val hero: Hero = Hero.viking){

    val maxDeckNumber: Int = 3

    var startHealth : Int = 0
        private set

    var currentHealth: Int = 0
        private set

    var currentMaxHealth: Int = 0
        private set

    var dealCardsPenalty : Int = 0
        private set

    init {
        when (difficulty){
            Difficulty.easy -> {
                startHealth = 12
                currentHealth = startHealth
                currentMaxHealth = startHealth
                dealCardsPenalty = 0
            }
            Difficulty.normal -> {
                startHealth = 12
                currentHealth = startHealth
                currentMaxHealth = startHealth
                dealCardsPenalty = 1
            }
            Difficulty.hard -> {
                startHealth = 11
                currentHealth = startHealth
                currentMaxHealth = startHealth
                dealCardsPenalty = 1
            }
        }
    }
}