/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.Enums

import androidx.annotation.Keep

@Keep
enum class Area(val area: String) {
    dungeon("dungeon"),
    equipped("equipped"),
    backpack("backpack"),
    discard("discard"),
    hero("hero"),
    none("none")
}