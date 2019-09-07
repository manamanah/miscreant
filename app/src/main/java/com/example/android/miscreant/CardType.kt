/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant

enum class CardType(val id: String) {
    NONE("none"),
    MONSTER("monster"),
    HERO("hero"),
    WEAPON("weapon"),
    SHIELD("shield"),
    POTION("potion");

    companion object{
        private val map = values().associateBy(CardType::id)

        fun getTypeFromString(name: String) : CardType{
            return if (map.containsKey(name)){
                map[name] ?: NONE
            }
            else NONE
        }
    }
}