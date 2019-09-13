/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant

import com.example.android.miscreant.Enums.CardType
import com.example.android.miscreant.Enums.Difficulty
import com.example.android.miscreant.Enums.Hero
import com.example.android.miscreant.Enums.Location
import com.example.android.miscreant.models.Card

class Settings(val difficulty: Difficulty = Difficulty.easy, val heroName: String = "", val hero: Hero = Hero.viking, val maxDeckNumber: Int = 3, val maxSpecials: Int = 3){

    var usedSpecials: Int = 0
        private set

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
                dealCardsPenalty = 0
            }
            Difficulty.normal -> {
                startHealth = 12
                dealCardsPenalty = 1
            }
            Difficulty.hard -> {
                startHealth = 11
                dealCardsPenalty = 1
            }
        }

        currentHealth = startHealth
        currentMaxHealth = startHealth
        usedSpecials = if (hero == Hero.archer) maxSpecials else 0
    }

    fun getHeroCard(): Card {
        return Card(
            type = CardType.hero,
            name = heroName,
            health = currentHealth,
            image = hero.imageName,
            location = Location.hero
        )
    }

    fun updateHeroHealth(newHealth: Int){
        currentHealth = newHealth
    }

    fun updateHeroMaxHealth(addHealth: Int){
        currentMaxHealth = addHealth
    }

    fun updateUsedSpecials(){
        usedSpecials += 1
    }
}