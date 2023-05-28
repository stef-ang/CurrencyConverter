package com.stefang.app.feature.currency.viewmodel

import app.cash.turbine.test
import com.stefang.app.core.testing.logger.TestLogger
import com.stefang.app.core.testing.repository.TestCurrencyRepository
import com.stefang.app.core.testing.repository.TestHistoryRepository
import com.stefang.app.core.testing.util.MainDispatcherRule
import com.stefang.app.feature.currency.model.CurrencyUiModel
import com.stefang.app.feature.currency.usecase.GetAllCurrenciesUseCase
import com.stefang.app.feature.currency.usecase.GetAllExchangeResultsUseCase
import com.stefang.app.feature.currency.usecase.currenciesExpected
import com.stefang.app.feature.currency.usecase.currenciesInput
import com.stefang.app.feature.currency.usecase.ratesExpected
import com.stefang.app.feature.currency.usecase.ratesInput
import com.stefang.app.feature.currency.utils.TestAppDispatchers
import com.stefang.app.feature.currency.viewmodel.CurrencyConverterViewModel.SnackBarEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.IOException
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyConverterViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val currencyRepository = TestCurrencyRepository()
    private val getAllCurrenciesUseCase = GetAllCurrenciesUseCase(currencyRepository)
    private val getAllExchangeResultsUseCase = GetAllExchangeResultsUseCase(currencyRepository)
    private val logger = TestLogger()
    private val historyRepository = TestHistoryRepository()
    private val testAppDispatchers = TestAppDispatchers()

    private lateinit var viewModel: CurrencyConverterViewModel

    /**
     * Manual setUp test without @Before because
     * whenViewModelInit_thenTryUpdateCurrenciesAndRates_butGotException needs custom setup before setUp
     */
    private fun setUp() {
        viewModel = CurrencyConverterViewModel(
            currencyRepository,
            getAllCurrenciesUseCase,
            getAllExchangeResultsUseCase,
            logger,
            historyRepository,
            testAppDispatchers
        )
    }

    @Test
    fun whenViewModelInit_thenTryUpdateCurrenciesAndRates() = runTest {
        setUp()
        assertEquals(true, currencyRepository.hasTriedUpdateCurrenciesAndRates)
    }

    @Test
    fun whenViewModelInit_thenTryUpdateCurrenciesAndRates_butGotException() = runTest {
        currencyRepository.setShouldThrowsError(true)
        val job = launch {
            viewModel.snackBarEvent.test {
                val emission = awaitItem()
                assertEquals(SnackBarEvent.NetworkError, emission)
                cancelAndConsumeRemainingEvents()
            }
        }
        setUp()
        job.join()
        assertTrue(logger.onErrorCalled)
    }

    @Test
    fun whenCollectAllCurrencies_thenGotListCurrencyUiModel() = runTest {
        setUp()
        val collectJob = launch(dispatcherRule.testDispatcher) { viewModel.allCurrencies.collect() }

        currencyRepository.sendCurrenciesModel(currenciesInput)

        val allCurrencies = viewModel.allCurrencies.value
        assertEquals(currenciesExpected.size, allCurrencies.size)
        currenciesExpected.forEachIndexed { index, expected ->
            assertEquals(expected.code, allCurrencies[index].code)
            assertEquals(expected.text, allCurrencies[index].text)
        }

        collectJob.cancel()
    }

    @Test
    fun whenCollectAllCurrencies_butItsEmpty_thenGotEmptyList() = runTest {
        setUp()
        val collectJob = launch(dispatcherRule.testDispatcher) { viewModel.allCurrencies.collect() }

        currencyRepository.sendCurrenciesModel(emptyList())

        assertEquals(0, viewModel.allCurrencies.value.size)

        collectJob.cancel()
    }

    @Test
    fun whenCollectAllExchangeResults_butSourceCurrencyIsNull_thenGotEmptyList() = runTest {
        setUp()
        val collectJob = launch(dispatcherRule.testDispatcher) { viewModel.allExchangeResults.collect() }

        currencyRepository.sendCurrencyRatesModel(ratesInput)
        viewModel.updateAmount("12300")

        assertEquals(0, viewModel.allExchangeResults.value.size)

        collectJob.cancel()
    }

    @Test
    fun whenCollectAllExchangeResults_butAmountIs0_thenGotEmptyList() = runTest {
        setUp()
        val collectJob = launch(dispatcherRule.testDispatcher) { viewModel.allExchangeResults.collect() }

        currencyRepository.sendCurrencyRatesModel(ratesInput)
        viewModel.updateSourceCurrency(CurrencyUiModel("USD", "USD - usd"))
        viewModel.updateAmount("0")

        assertEquals(0, viewModel.allExchangeResults.value.size)

        collectJob.cancel()
    }

    @Test
    fun whenCollectAllExchangeResults_andAmountAndSourceAreValid_thenGotListExchangeResultUiModel() = runTest {
        setUp()
        val collectJob = launch(dispatcherRule.testDispatcher) { viewModel.allExchangeResults.collect() }

        currencyRepository.sendCurrencyRatesModel(ratesInput)
        viewModel.updateSourceCurrency(CurrencyUiModel("ABC", "ABC - abc"))
        viewModel.updateAmount("10000")

        val result = viewModel.allExchangeResults.value
        assertEquals(ratesExpected.size, result.size)
        ratesExpected.forEachIndexed { index, expected ->
            assertEquals(expected.code, result[index].code)
            assertEquals(expected.amount, result[index].amount)
        }

        collectJob.cancel()
    }

    @Test
    fun whenTrackHistory_thenHistoryRepositoryHasLatestRecord() = runTest {
        historyRepository.histories.test {
            setUp()
            viewModel.trackHistory("USD", 10000)
            testAppDispatchers.advanceUntilIdle()
            val history = awaitItem().first()
            assertEquals("USD", history.code)
            assertEquals(10000, history.amount)

            cancelAndConsumeRemainingEvents()
        }
    }

    @Test
    fun whenTrackHistoryHappenTwice_thenHistoryRepositoryHasLatestRecord() = runTest {
        historyRepository.histories.test {
            setUp()
            viewModel.trackHistory("USD", 10000)
            viewModel.trackHistory("IDR", 20000)
            testAppDispatchers.advanceUntilIdle()
            val history = awaitItem().first()
            assertEquals("IDR", history.code)
            assertEquals(20000, history.amount)

            cancelAndConsumeRemainingEvents()
        }
    }
}
