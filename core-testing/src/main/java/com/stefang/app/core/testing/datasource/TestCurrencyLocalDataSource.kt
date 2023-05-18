package com.stefang.app.core.testing.datasource

import com.stefang.app.core.database.datasource.CurrencyLocalDataSource
import com.stefang.app.core.database.entity.CurrencyDbModel
import com.stefang.app.core.database.entity.ExchangeRatesDbModel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.map

class TestCurrencyLocalDataSource: CurrencyLocalDataSource {

    private val allCurrenciesFlow: MutableSharedFlow<List<CurrencyDbModel>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private val allExchangeRatesFlow: MutableSharedFlow<List<ExchangeRatesDbModel>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override fun getAllCurrencies(): Flow<List<CurrencyDbModel>> {
        return allCurrenciesFlow
    }

    override suspend fun getCurrency(code: String): CurrencyDbModel? {
        return null
    }

    override fun saveCurrencies(currencies: List<CurrencyDbModel>) {
        allCurrenciesFlow.tryEmit(currencies)
    }

    override fun getAllExchangeRatesByBase(base: String): Flow<List<ExchangeRatesDbModel>> {
        return allExchangeRatesFlow.map { list ->
            return@map list.filter { it.base == base }
        }
    }

    override fun saveExchangeRates(exchangeRateList: List<ExchangeRatesDbModel>) {
        allExchangeRatesFlow.tryEmit(exchangeRateList)
    }
}
