package com.stefang.app.core.api

import com.stefang.app.core.api.model.ExchangeRatesApiModel
import retrofit2.http.GET
import retrofit2.http.Query

interface OpenExchangeApi {

    @GET("currencies.json")
    suspend fun getCurrencies(): Map<String, String>

    @GET("latest.json")
    suspend fun getLatestExchangeRate(
        @Query("app_id") appId: String = BuildConfig.OPEN_EXCHANGE_API_KEY
    ): ExchangeRatesApiModel
}
