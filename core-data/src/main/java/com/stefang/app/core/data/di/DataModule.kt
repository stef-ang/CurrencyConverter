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

package com.stefang.app.core.data.di

import android.content.Context
import com.stefang.app.core.data.CurrencyRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import com.stefang.app.core.data.DataItemTypeRepository
import com.stefang.app.core.data.DefaultDataItemTypeRepository
import com.stefang.app.core.data.OfflineFirstCurrencyRepositoryImpl
import com.stefang.app.core.data.datastore.ExchangeRateDataStore
import com.stefang.app.core.data.datastore.ExchangeRateDataStoreImpl
import com.stefang.app.core.data.date.TimeHelper
import com.stefang.app.core.data.date.TimeHelperImpl
import dagger.Provides
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindsDataItemTypeRepository(
        dataItemTypeRepository: DefaultDataItemTypeRepository
    ): DataItemTypeRepository

    @Singleton
    @Binds
    fun bindsCurrencyRepository(
        currencyRepository: OfflineFirstCurrencyRepositoryImpl
    ): CurrencyRepository

    @Singleton
    @Binds
    fun bindsTimeHelper(timeHelper: TimeHelperImpl): TimeHelper

    companion object {

        @Singleton
        @Provides
        fun provideExchangeRateDataStore(@ApplicationContext appContext: Context): ExchangeRateDataStore {
            return ExchangeRateDataStoreImpl(appContext)
        }
    }
}

class FakeDataItemTypeRepository @Inject constructor() : DataItemTypeRepository {
    override val dataItemTypes: Flow<List<String>> = flowOf(fakeDataItemTypes)

    override suspend fun add(name: String) {
        throw NotImplementedError()
    }
}

val fakeDataItemTypes = listOf("One", "Two", "Three")
