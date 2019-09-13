/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.models

data class ImpactOutput(
    var firstCard: Card = Card(),
    var secondCard: Card = Card(),
    var currentHealth: Int = -1,
    var maxHealth: Int = -1,
    var showPotentialHealth: Boolean = false,
    var showPotentialMaxHealth: Boolean = false,
    val specialUsed: Boolean = false,
    val potentialSpecialUse: Boolean = false
)