/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant

import android.view.View
import com.example.android.miscreant.Enums.CardType
import com.example.android.miscreant.Enums.Location

fun View.getCardLocationByName() : Location {
    val cardPosition = this.toString().split("app:id/card_")[1].dropLast(1)
    return Location.getLocationFromString(cardPosition)
}

fun View.getCardTypeFromTag() : CardType {
    return CardType.getTypeFromString(this.tag.toString())
}