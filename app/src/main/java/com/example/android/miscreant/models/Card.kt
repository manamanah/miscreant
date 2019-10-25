/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.models

import com.example.android.miscreant.Enums.CardType
import com.example.android.miscreant.Enums.Location

data class Card(
    val type: CardType = CardType.none,
    val name: String = "",
    var health: Int = 0,
    val image: String = "",
    val counterAttackValue: Int = 1,
    var potentialHealth: Int = 0,
    var location: Location = Location.none,
    var showHealth: Boolean = false,
    var showRIP: Boolean = false,
    var showConsumed: Boolean = false,
    var showPotentialHealth: Boolean = false,
    var isHighlightOn: Boolean = false,
    var showCounterAttackIntent: Boolean = false,
    var triggerCounterAttackAnimation: Boolean = false,
    var isLookActive: Boolean = true,
    var showEquip: Boolean = false,
    var triggerClawAnimation: Boolean = false,
    var triggerHitAnimation: Boolean = false,
    var triggerCounterHitAnimation: Boolean = false
) {

    fun isEmpty(): Boolean {
        return type == CardType.none
    }
}