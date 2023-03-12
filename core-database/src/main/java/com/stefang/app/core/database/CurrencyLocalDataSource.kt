package com.stefang.app.core.database

import com.stefang.app.core.database.entity.CurrencyDbModel
import com.stefang.app.core.database.entity.ExchangeRatesBdModel
import kotlinx.coroutines.flow.Flow

interface CurrencyLocalDataSource {

    fun getAllCurrencies(): Flow<List<CurrencyDbModel>>

    fun saveCurrencies(currencies: List<CurrencyDbModel>)

    fun getAllExchangeRatesByBase(base: String): Flow<List<ExchangeRatesBdModel>>

    fun saveExchangeRates(exchangeRateList: List<ExchangeRatesBdModel>)
}
