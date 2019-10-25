/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.models

import com.example.android.miscreant.Enums.Area
import com.example.android.miscreant.Enums.CardType
import com.example.android.miscreant.Enums.Location

data class SelectedCard(
    var location: Location = Location.none,
    val type: CardType = CardType.none,
    var inArea: Area = Area.none
) {

    init {
        inArea = getArea(location)
    }

    fun isEmpty(): Boolean {
        return location == Location.none && type == CardType.none
    }

    private fun getArea(location: Location): Area {
        return when {
            location == Location.backpack -> Area.backpack
            location == Location.hero -> Area.hero
            location == Location.discard -> Area.discard
            isInDungeon(location) -> Area.dungeon
            isEquipped(location) -> Area.equipped
            else -> Area.none
        }
    }

    private fun isEquipped(location: Location): Boolean {
        return location == Location.equip_left || location == Location.equip_right
    }

    private fun isInDungeon(location: Location): Boolean {
        return location == Location.dungeon_left_back ||
                location == Location.dungeon_middle_back ||
                location == Location.dungeon_right_back ||
                location == Location.dungeon_left_front ||
                location == Location.dungeon_middle_front ||
                location == Location.dungeon_right_front
    }
}