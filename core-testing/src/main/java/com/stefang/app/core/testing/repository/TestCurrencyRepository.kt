package com.stefang.app.core.testing.repository

import com.stefang.app.core.data.repository.CurrencyRepository
import com.stefang.app.core.data.model.CurrencyModel
import com.stefang.app.core.data.model.CurrencyRateModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import java.io.IOException

class TestCurrencyRepository : CurrencyRepository {

    private val currenciesFlow: MutableSharedFlow<List<CurrencyModel>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val exchangeRatesFlow: MutableSharedFlow<List<CurrencyRateModel>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override val currencies: Flow<List<CurrencyModel>>
        get() = currenciesFlow

    override val exchangeRates: Flow<List<CurrencyRateModel>>
        get() = exchangeRatesFlow

    fun sendCurrenciesModel(list: List<CurrencyModel>) {
        currenciesFlow.tryEmit(list)
    }

    fun sendCurrencyRatesModel(list: List<CurrencyRateModel>) {
        exchangeRatesFlow.tryEmit(list)
    }

    var hasTriedUpdateCurrenciesAndRates = false

    private var shouldThrowsError = false

    fun setShouldThrowsError(value: Boolean) {
        shouldThrowsError = value
    }

    override suspend fun tryUpdateCurrenciesAndRates() {
        if (shouldThrowsError) throw IOException()
        hasTriedUpdateCurrenciesAndRates = true
    }
}
