/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.models

import com.example.android.miscreant.CardType

data class Card(val type: CardType = CardType.NONE,
                val name: String = "",
                var value: Int = 0,
                val image: String = "",
                var potentialValue: Int = 0,
                var showValue: Boolean = false,
                var showRIP: Boolean = false,
                var showPotentialValue: Boolean = false,
                var isBgColorOn: Boolean = false,
                var isAttackVisible: Boolean = false){

    fun isEmpty(): Boolean {
        return type == CardType.NONE
    }
}