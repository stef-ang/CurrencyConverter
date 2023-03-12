package com.stefang.app.core.database.di

import com.stefang.app.core.database.CurrencyLocalDataSource
import com.stefang.app.core.database.CurrencyLocalDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataSourceModule {

    @Binds
    fun provideCurrencyLocalDataSource(
        currencyLocalDataSource: CurrencyLocalDataSourceImpl
    ): CurrencyLocalDataSource
}
