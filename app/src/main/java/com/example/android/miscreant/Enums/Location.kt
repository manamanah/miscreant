/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.Enums

enum class Location(val title: String){
    dungeon_left_back("dungeon_left_back"),
    dungeon_middle_back("dungeon_middle_back"),
    dungeon_right_back("dungeon_right_back"),
    dungeon_left_front("dungeon_left_front"),
    dungeon_middle_front("dungeon_middle_front"),
    dungeon_right_front("dungeon_right_front"),
    equip_left("equip_left"),
    equip_right("equip_right"),
    hero("hero"),
    discard("discard"),
    backpack("backpack"),
    none("none");

    companion object {
        private val map: Map<String, Location> = values().associateBy(
            Location::title)

        private var area = Area.none

        fun getLocationFromString(name: String): Location {
            var location = none

            if (map.containsKey(name)) {
                location = map[name] ?: none
            }

            return location
        }
    }
}