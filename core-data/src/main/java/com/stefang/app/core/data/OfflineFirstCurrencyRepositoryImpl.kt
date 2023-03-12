package com.stefang.app.core.data

import com.stefang.app.core.api.CurrencyRemoteDataSource
import com.stefang.app.core.data.datastore.ExchangeRateDataStore
import com.stefang.app.core.data.date.TimeHelper
import com.stefang.app.core.data.mapper.toDbModel
import com.stefang.app.core.data.mapper.toModel
import com.stefang.app.core.data.model.CurrencyModel
import com.stefang.app.core.data.model.CurrencyRateModel
import com.stefang.app.core.database.CurrencyLocalDataSource
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class OfflineFirstCurrencyRepositoryImpl @Inject constructor(
    private val dataStore: ExchangeRateDataStore,
    private val localDataSource: CurrencyLocalDataSource,
    private val remoteDataSource: CurrencyRemoteDataSource,
    private val timeHelper: TimeHelper
) : CurrencyRepository {

    override val currencies: Flow<List<CurrencyModel>>
        get() = localDataSource.getAllCurrencies().map { list ->
            list.map { it.toModel() }
        }

    @OptIn(FlowPreview::class)
    override val exchangeRates: Flow<List<CurrencyRateModel>>
        get() = dataStore.latestBase.flatMapConcat { base ->
            localDataSource.getAllExchangeRatesByBase(base).map { list ->
                list.map { it.toModel() }
            }
        }

    private suspend fun isLocalDataNotValid(): Boolean {
        val thirtyMinutes = 30 * 60 * 1000
        return dataStore.lastUpdate.first()?.let { lastUpdate ->
            timeHelper.currentTimeMillis - lastUpdate >= thirtyMinutes
        } ?: true
    }

    override suspend fun tryUpdateCurrenciesAndRates() {
        if (isLocalDataNotValid()) {
            coroutineScope {
                val deferredCurrencies = async { remoteDataSource.getCurrencies() }
                val deferredRates = async { remoteDataSource.getLatestExchangeRate() }

                localDataSource.saveCurrencies(deferredCurrencies.await().toDbModel())
                val exchangeRate = deferredRates.await()
                localDataSource.saveExchangeRates(exchangeRate.toDbModel())
                dataStore.setLastUpdate(exchangeRate.timestamp)
                dataStore.setLatestBase(exchangeRate.base)
            }
        }
    }
}
