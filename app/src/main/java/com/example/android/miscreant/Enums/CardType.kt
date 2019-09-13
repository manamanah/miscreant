/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.Enums

enum class CardType(val id: String) {
    none("none"),
    monster("monster"),
    hero("hero"),
    weapon("weapon"),
    shield("shield"),
    potion("potion"),
    backpack("backpack"),
    discard("discard");

    companion object{
        private val map = values().associateBy(CardType::id)

        fun getTypeFromString(name: String): CardType {
            return if (map.containsKey(name)){
                map[name] ?: none
            } else {
                none
            }
        }
    }
}