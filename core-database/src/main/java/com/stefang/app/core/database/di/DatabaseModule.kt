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

package com.stefang.app.core.database.di

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.stefang.app.core.database.AppDatabase
import com.stefang.app.core.database.dao.CurrencyDao
import com.stefang.app.core.database.dao.ExchangeRatesDao
import com.stefang.app.core.database.dao.HistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    fun provideCurrencyDao(appDatabase: AppDatabase): CurrencyDao {
        return appDatabase.currencyDao()
    }

    @Provides
    fun provideExchangeRatesDao(appDatabase: AppDatabase): ExchangeRatesDao {
        return appDatabase.exchangeRatesDao()
    }

    @Provides
    fun provideHistoryDao(appDatabase: AppDatabase): HistoryDao {
        return appDatabase.historyDao()
    }

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            DB_NAME
        ).addMigrations(MIGRATION_1_2, MIGRATION_2_3).build()
    }

    companion object {
        private const val DB_NAME = "converter-currency-db"

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create the new table
                database.execSQL(
                    """
                    CREATE TABLE history (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        code TEXT NOT NULL,
                        name TEXT NOT NULL,
                        rate REAL NOT NULL,
                        inserted_at INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Create new temporary table with the desired schema
                database.execSQL(
                    """
                    CREATE TABLE history_new (
                        id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        code TEXT NOT NULL,
                        name TEXT NOT NULL,
                        amount INTEGER NOT NULL,
                        inserted_at INTEGER NOT NULL
                    )
                    """.trimIndent()
                )

                // Copy the data from the old table to the new one, converting 'rate' to an integer
                database.execSQL(
                    """
                    INSERT INTO history_new (id, code, name, amount, inserted_at)
                    SELECT id, code, name, CAST(rate AS INTEGER), inserted_at
                    FROM history
                    """.trimIndent()
                )

                // Remove the old table
                database.execSQL("DROP TABLE history")

                // Rename the new table to the old one
                database.execSQL("ALTER TABLE history_new RENAME TO history")
            }
        }
    }
}
