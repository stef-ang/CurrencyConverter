package com.stefang.app.core.api

import com.stefang.app.core.api.model.ExchangeRatesApiModel

interface CurrencyRemoteDataSource {
    suspend fun getCurrencies(): Map<String, String>

    suspend fun getLatestExchangeRate(): ExchangeRatesApiModel
}
