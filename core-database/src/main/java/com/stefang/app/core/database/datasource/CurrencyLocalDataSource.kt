package com.stefang.app.core.database.datasource

import com.stefang.app.core.database.entity.CurrencyDbModel
import com.stefang.app.core.database.entity.ExchangeRatesDbModel
import kotlinx.coroutines.flow.Flow

interface CurrencyLocalDataSource {

    fun getAllCurrencies(): Flow<List<CurrencyDbModel>>

    suspend fun getCurrency(code: String): CurrencyDbModel?

    fun saveCurrencies(currencies: List<CurrencyDbModel>)

    fun getAllExchangeRatesByBase(base: String): Flow<List<ExchangeRatesDbModel>>

    fun saveExchangeRates(exchangeRateList: List<ExchangeRatesDbModel>)
}
