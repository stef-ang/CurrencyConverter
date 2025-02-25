package com.stefang.app.core.data.repository

import com.stefang.app.core.api.CurrencyRemoteDataSource
import com.stefang.app.core.data.datastore.ExchangeRateDataStore
import com.stefang.app.core.data.date.TimeHelper
import com.stefang.app.core.data.mapper.toDbModel
import com.stefang.app.core.data.mapper.toModel
import com.stefang.app.core.data.model.CurrencyModel
import com.stefang.app.core.data.model.CurrencyRateModel
import com.stefang.app.core.database.datasource.CurrencyLocalDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.isActive
import kotlinx.coroutines.withContext
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
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

    override val exchangeRates: Flow<List<CurrencyRateModel>>
        get() = dataStore.latestBase.flatMapLatest { base ->
            localDataSource.getAllExchangeRatesByBase(base).map { list ->
                list.map { it.toModel() }
            }
        }

    private suspend fun isLocalDataNotValid(): Boolean {
        return dataStore.lastUpdate.firstOrNull()?.let { lastUpdate ->
            timeHelper.currentTimeMillis - lastUpdate >= CACHE_LIMIT_DURATION
        } ?: true
    }

    // switch to Dispatchers.IO to avoid crash in Room operation
    override suspend fun tryUpdateCurrenciesAndRates(): Unit = withContext(Dispatchers.IO) {
        if (isLocalDataNotValid()) {
            val deferredCurrencies = async { remoteDataSource.getCurrencies() }
            val deferredRates = async { remoteDataSource.getLatestExchangeRate() }

            localDataSource.saveCurrencies(deferredCurrencies.await().toDbModel())
            val exchangeRate = deferredRates.await()
            localDataSource.saveExchangeRates(exchangeRate.toDbModel())
            dataStore.setLastUpdate(timeHelper.currentTimeMillis)
            dataStore.setLatestBase(exchangeRate.base)
        }
    }

    companion object {
        // 30 minutes limit duration
        private const val CACHE_LIMIT_DURATION = 30 * 60 * 1000
    }
}
