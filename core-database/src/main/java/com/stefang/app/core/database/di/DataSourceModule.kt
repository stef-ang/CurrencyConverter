package com.stefang.app.core.database.di

import com.stefang.app.core.database.datasource.CurrencyLocalDataSource
import com.stefang.app.core.database.datasource.CurrencyLocalDataSourceImpl
import com.stefang.app.core.database.datasource.HistoryDataSource
import com.stefang.app.core.database.datasource.HistoryDataSourceImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface DataSourceModule {

    @Binds
    fun bindsCurrencyLocalDataSource(
        currencyLocalDataSource: CurrencyLocalDataSourceImpl
    ): CurrencyLocalDataSource

    @Binds
    fun bindsHistoryDataSource(dataSource: HistoryDataSourceImpl): HistoryDataSource
}
