package com.stefang.app.feature.currency.usecase

import com.stefang.app.core.data.model.CurrencyRateModel
import com.stefang.app.core.testing.repository.TestCurrencyRepository
import com.stefang.app.feature.currency.model.ExchangeResultUiModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetAllExchangeResultsUseCaseTest {

    private val currencyRepository = TestCurrencyRepository()
    private val useCase = GetAllExchangeResultsUseCase(currencyRepository)

    @Test
    fun whenExchangeRatesExist_thenGotAllExchangeResultUiModel() = runBlocking {
        currencyRepository.sendCurrencyRatesModel(ratesInput)

        val result = useCase("ABC", 10_000.0).first()

        assertEquals(ratesExpected.size, result.size)
        ratesExpected.forEachIndexed { index, expected ->
            assertEquals(expected.code, result[index].code)
            assertEquals(expected.amount, result[index].amount)
        }
    }

    @Test
    fun whenExchangeRatesExist_butSourceNull_thenGotEmptyExchangeResultUiModel() = runBlocking {
        currencyRepository.sendCurrencyRatesModel(ratesInput)

        val result = useCase(null, 999.0).first()

        assertEquals(0, result.size)
    }

    @Test
    fun whenExchangeRatesExist_butSourceInvalid_thenGotEmptyExchangeResultUiModel() = runBlocking {
        currencyRepository.sendCurrencyRatesModel(ratesInput)

        val result = useCase("XYZ", 555.0).first()

        assertEquals(0, result.size)
    }
}

val ratesInput = listOf(
    CurrencyRateModel("CHF", "USD", 0.92006),
    CurrencyRateModel("BND", "USD", 1.348212),
    CurrencyRateModel("ABC", "USD", 3.0)
)

val ratesExpected = listOf(
    ExchangeResultUiModel("CHF", "3,066.87"),
    ExchangeResultUiModel("BND", "4,494.04"),
    ExchangeResultUiModel("ABC", "10,000")
)
