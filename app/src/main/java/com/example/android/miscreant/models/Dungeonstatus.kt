/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.models

import com.example.android.miscreant.Enums.Location

data class Dungeonstatus(val emptySpots: MutableList<Location> = mutableListOf(), var monstersInDungeon: Int = 0, var counterAttackOpen: Int = 0)