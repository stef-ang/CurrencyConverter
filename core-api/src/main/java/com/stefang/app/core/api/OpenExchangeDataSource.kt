package com.stefang.app.core.api

import com.stefang.app.core.api.model.ExchangeRatesApiModel
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OpenExchangeDataSource @Inject constructor(retrofit: Retrofit) : CurrencyRemoteDataSource {

    private val openExchangeApi = retrofit.create(OpenExchangeApi::class.java)

    override suspend fun getCurrencies(): Map<String, String> {
        return openExchangeApi.getCurrencies()
    }

    override suspend fun getLatestExchangeRate(): ExchangeRatesApiModel {
        return openExchangeApi.getLatestExchangeRate()
    }
}
