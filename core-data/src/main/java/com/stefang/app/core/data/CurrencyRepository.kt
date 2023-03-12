package com.stefang.app.core.data

import com.stefang.app.core.data.model.CurrencyModel
import com.stefang.app.core.data.model.CurrencyRateModel
import kotlinx.coroutines.flow.Flow

interface CurrencyRepository {

    val currencies: Flow<List<CurrencyModel>>

    val exchangeRates: Flow<List<CurrencyRateModel>>

    suspend fun tryUpdateCurrenciesAndRates()
}
