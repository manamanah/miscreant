/*
 * Copyright (c) 2019. Birgit Schoenauer
 *
 * Licensed under the Creative Commons Attribution-NonCommercial-NonDerivatives 4.0 International License.
 * See file 'LICENSE.md' or https://creativecommons.org/licenses/by-nc-nd/4.0/ for full license details.
 */

package com.example.android.miscreant.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class InitialMigration: Migration(1, 2){

    override fun migrate(database: SupportSQLiteDatabase) {
        // initial creation: no need for migration
    }
}