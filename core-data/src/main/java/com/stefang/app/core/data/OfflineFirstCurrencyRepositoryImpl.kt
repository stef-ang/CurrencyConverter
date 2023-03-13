package com.stefang.app.core.data

import android.util.Log
import com.stefang.app.core.api.CurrencyRemoteDataSource
import com.stefang.app.core.data.datastore.ExchangeRateDataStore
import com.stefang.app.core.data.date.TimeHelper
import com.stefang.app.core.data.mapper.toDbModel
import com.stefang.app.core.data.mapper.toModel
import com.stefang.app.core.data.model.CurrencyModel
import com.stefang.app.core.data.model.CurrencyRateModel
import com.stefang.app.core.database.CurrencyLocalDataSource
import com.stefang.app.core.database.entity.ExchangeRatesBdModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.flow.lastOrNull
import kotlinx.coroutines.flow.map
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
            Log.d("allExchangeResults", "base: $base")
            localDataSource.getAllExchangeRatesByBase(base).map { list ->
                list.map { it.toModel() }
            }
        }

    private suspend fun isLocalDataNotValid(): Boolean {
        val thirtyMinutes = 30 * 60 * 1000
        return dataStore.lastUpdate.lastOrNull()?.let { lastUpdate ->
            timeHelper.currentTimeMillis - lastUpdate >= thirtyMinutes
        } ?: true
    }

    override suspend fun tryUpdateCurrenciesAndRates() = withContext(Dispatchers.IO) {
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
