package com.stefang.app.core.data

import com.stefang.app.core.data.date.TimeHelper
import com.stefang.app.core.data.repository.OfflineFirstCurrencyRepositoryImpl
import com.stefang.app.core.database.entity.CurrencyDbModel
import com.stefang.app.core.database.entity.ExchangeRatesDbModel
import com.stefang.app.core.testing.datasource.TestCurrencyLocalDataSource
import com.stefang.app.core.testing.datasource.TestCurrencyRemoteDataSource
import com.stefang.app.core.testing.datasource.currenciesMap
import com.stefang.app.core.testing.datasource.ratesMap
import com.stefang.app.core.testing.datastore.TestExchangeRateDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class OfflineFirstCurrencyRepositoryImplTest {

    private val dataStore = TestExchangeRateDataStore()
    private val localDataStore = TestCurrencyLocalDataSource()
    private val remoteDataSource = TestCurrencyRemoteDataSource()

    private lateinit var repository: OfflineFirstCurrencyRepositoryImpl

    @Before
    fun before() = runBlocking {
        dataStore.setLatestBase("ABC")
        localDataStore.saveCurrencies(emptyList())
        localDataStore.saveExchangeRates(emptyList())

        repository = OfflineFirstCurrencyRepositoryImpl(
            dataStore,
            localDataStore,
            remoteDataSource,
            object : TimeHelper {
                override val currentTimeMillis: Long = 0
            }
        )
    }

    @Test
    fun whenAllCurrenciesDoesntExist_thenGotEmptyList() = runBlocking {
        assertEquals(0, repository.currencies.first().size)
    }

    @Test
    fun whenAllCurrenciesExist_thenGotListCurrencyModel() = runBlocking {
        localDataStore.saveCurrencies(currenciesDbModelInput)

        val result = repository.currencies.first()
        assertEquals(currenciesDbModelInput.size, result.size)
        currenciesDbModelInput.forEachIndexed { index, expected ->
            assertEquals(expected.code, result[index].code)
            assertEquals(expected.name, result[index].name)
        }
    }

    @Test
    fun whenBaseDoesntExistInRates_thenGotEmptyList() = runBlocking {
        localDataStore.saveExchangeRates(ratesDbModelInput)

        assertEquals(0, repository.exchangeRates.first().size)
    }

    @Test
    fun whenBaseExistInRates_thenGotListCurrencyRateModel() = runBlocking {
        dataStore.setLatestBase("USD")
        localDataStore.saveExchangeRates(ratesDbModelInput)

        val result = repository.exchangeRates.first()
        assertEquals(ratesDbModelInput.size, result.size)
        ratesDbModelInput.forEachIndexed { index, expected ->
            assertEquals(expected.code, result[index].code)
            assertEquals(expected.base, result[index].base)
            assertEquals(expected.rate, result[index].rate, 0.01)
        }
    }

    @Test
    fun whenLocalDbIsValid_thenDontRequestApi_andLocalDbRemainsSame() = runBlocking {
        dataStore.setLastUpdate(1_678_000_000_000)
        dataStore.setLatestBase("USD")
        localDataStore.saveCurrencies(currenciesDbModelInput)
        localDataStore.saveExchangeRates(ratesDbModelInput)
        repository = OfflineFirstCurrencyRepositoryImpl(
            dataStore,
            localDataStore,
            remoteDataSource,
            object : TimeHelper {
                // difference: 1_000_000, limit difference: 1_800_000
                override val currentTimeMillis: Long = 1_678_001_000_000
            }
        )

        repository.tryUpdateCurrenciesAndRates()

        assertEquals(currenciesDbModelInput.size, repository.currencies.first().size)
        assertEquals(ratesDbModelInput.size, repository.exchangeRates.first().size)
    }

    @Test
    fun whenLocalDbIsNotValid_thenRequestApi_andLocalDbUpdated() = runBlocking {
        dataStore.setLastUpdate(1_678_000_000_000)
        localDataStore.saveCurrencies(currenciesDbModelInput)
        localDataStore.saveExchangeRates(ratesDbModelInput)
        repository = OfflineFirstCurrencyRepositoryImpl(
            dataStore,
            localDataStore,
            remoteDataSource,
            object : TimeHelper {
                // difference: 2_000_000, limit difference: 1_800_000
                override val currentTimeMillis: Long = 1_678_002_000_000
            }
        )

        repository.tryUpdateCurrenciesAndRates()

        assertEquals(currenciesMap.size, repository.currencies.first().size)
        assertEquals(ratesMap.size, repository.exchangeRates.first().size)
        assertEquals(1_678_002_000_000, dataStore.lastUpdate.first())
        assertEquals("USD", dataStore.latestBase.first())
    }

    @Test
    fun whenLocalDbEmpty_thenRequestApi_andLocalDbUpdated() = runBlocking {
        // LocalDb Empty happens on fresh installed app and dataStore.lastUpdate has not been set
        repository = OfflineFirstCurrencyRepositoryImpl(
            dataStore,
            localDataStore,
            remoteDataSource,
            object : TimeHelper {
                override val currentTimeMillis: Long = 1_678_002_000_000
            }
        )

        repository.tryUpdateCurrenciesAndRates()

        assertEquals(currenciesMap.size, repository.currencies.first().size)
        assertEquals(ratesMap.size, repository.exchangeRates.first().size)
        assertEquals(1_678_002_000_000, dataStore.lastUpdate.first())
        assertEquals("USD", dataStore.latestBase.first())
    }
}

val currenciesDbModelInput = listOf(
    CurrencyDbModel("AED", "United Arab Emirates Dirham"),
    CurrencyDbModel("AFN", "Afghan Afghani"),
    CurrencyDbModel("ALL", "Albanian Lek"),
)

val ratesDbModelInput = listOf(
    ExchangeRatesDbModel("CHF", "USD", 0.92006),
    ExchangeRatesDbModel("BND", "USD", 1.348212),
    ExchangeRatesDbModel("ABC", "USD", 3.0),
    ExchangeRatesDbModel("USD", "USD", 1.0),
)
