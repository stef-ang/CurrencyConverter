/*
 * Copyright (C) 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stefang.app.core.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import com.stefang.app.core.database.dao.CurrencyDao
import com.stefang.app.core.database.dao.ExchangeRatesDao
import com.stefang.app.core.database.dao.HistoryDao
import com.stefang.app.core.database.entity.CurrencyDbModel
import com.stefang.app.core.database.entity.ExchangeRatesDbModel
import com.stefang.app.core.database.entity.HistoryDbModel

@Database(
    exportSchema = true,
    entities = [
        CurrencyDbModel::class,
        ExchangeRatesDbModel::class,
        HistoryDbModel::class
    ],
    version = 2,
    autoMigrations = [
        AutoMigration(from = 1, to = 2, spec = DatabaseMigrations.Schema1to2::class)
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun currencyDao(): CurrencyDao
    abstract fun exchangeRatesDao(): ExchangeRatesDao

    abstract fun historyDao(): HistoryDao
}
