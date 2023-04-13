package com.stefang.app.feature.currency

import app.cash.turbine.test
import com.stefang.app.core.testing.logger.TestLogger
import com.stefang.app.core.testing.repository.TestCurrencyRepository
import com.stefang.app.core.testing.util.MainDispatcherRule
import com.stefang.app.feature.currency.viewmodel.CurrencyConverterViewModel.SnackBarEvent
import com.stefang.app.feature.currency.usecase.GetAllCurrenciesUseCase
import com.stefang.app.feature.currency.usecase.GetAllExchangeResultsUseCase
import com.stefang.app.feature.currency.usecase.currenciesExpected
import com.stefang.app.feature.currency.usecase.currenciesInput
import com.stefang.app.feature.currency.usecase.ratesExpected
import com.stefang.app.feature.currency.usecase.ratesInput
import com.stefang.app.feature.currency.viewmodel.CurrencyConverterViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalCoroutinesApi::class)
class CurrencyConverterViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val currencyRepository = TestCurrencyRepository()
    private val getAllCurrenciesUseCase = GetAllCurrenciesUseCase(currencyRepository)
    private val getAllExchangeResultsUseCase = GetAllExchangeResultsUseCase(currencyRepository)
    private val logger = TestLogger()

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
            logger
        )
    }

    @Test
    fun whenViewModelInit_thenTryUpdateCurrenciesAndRates() = runBlocking {
        setUp()
        assertEquals(true, currencyRepository.hasTriedUpdateCurrenciesAndRates)
    }

    @Test
    fun whenViewModelInit_thenTryUpdateCurrenciesAndRates_butGotException() = runBlocking {
        currencyRepository.setShouldThrowsError(true)
        val job = launch {
            viewModel.snackBarEvent.test {
                val emission = awaitItem()
                assertEquals(SnackBarEvent.NetworkError, emission)
                cancelAndConsumeRemainingEvents()
            }
        }
        try {
            setUp()
            job.join()
        } catch (e: Exception) {
            assert(e is IOException)
        }
    }

    @Test
    fun whenCollectAllCurrencies_thenGotListCurrencyUiModel() = runBlocking {
        setUp()
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.allCurrencies.collect() }

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
    fun whenCollectAllCurrencies_butItsEmpty_thenGotEmptyList() = runBlocking {
        setUp()
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.allCurrencies.collect() }

        currencyRepository.sendCurrenciesModel(emptyList())

        assertEquals(0, viewModel.allCurrencies.value.size)

        collectJob.cancel()
    }

    @Test
    fun whenCollectAllExchangeResults_butSourceCurrencyIsNull_thenGotEmptyList() = runBlocking {
        setUp()
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.allExchangeResults.collect() }

        currencyRepository.sendCurrencyRatesModel(ratesInput)
        viewModel.updateAmount("12300")

        assertEquals(0, viewModel.allExchangeResults.value.size)

        collectJob.cancel()
    }

    @Test
    fun whenCollectAllExchangeResults_butAmountIs0_thenGotEmptyList() = runBlocking {
        setUp()
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.allExchangeResults.collect() }

        currencyRepository.sendCurrencyRatesModel(ratesInput)
        viewModel.updateSourceCurrency("USD")
        viewModel.updateAmount("0")

        assertEquals(0, viewModel.allExchangeResults.value.size)

        collectJob.cancel()
    }

    @Test
    fun whenCollectAllExchangeResults_andAmountAndSourceAreValid_thenGotListExchangeResultUiModel() = runBlocking {
        setUp()
        val collectJob = launch(UnconfinedTestDispatcher()) { viewModel.allExchangeResults.collect() }

        currencyRepository.sendCurrencyRatesModel(ratesInput)
        viewModel.updateSourceCurrency("ABC")
        viewModel.updateAmount("10000")

        val result = viewModel.allExchangeResults.value
        assertEquals(ratesExpected.size, result.size)
        ratesExpected.forEachIndexed { index, expected ->
            assertEquals(expected.code, result[index].code)
            assertEquals(expected.amount, result[index].amount)
        }

        collectJob.cancel()
    }
}
