package com.stefang.app.feature.currency.usecase

import com.stefang.app.core.data.model.CurrencyModel
import com.stefang.app.core.testing.repository.TestCurrencyRepository
import com.stefang.app.feature.currency.model.CurrencyUiModel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class GetAllCurrenciesUseCaseTest {

    private val currencyRepository = TestCurrencyRepository()
    private val useCase = GetAllCurrenciesUseCase(currencyRepository)

    @Test
    fun whenCurrenciesExist_thenGotAllCurrencyUiModel() = runBlocking {
        currencyRepository.sendCurrenciesModel(currenciesInput)

        val result = useCase().first()

        assertEquals(currenciesExpected.size, result.size)
        currenciesExpected.forEachIndexed { index, expected ->
            assertEquals(expected.code, result[index].code)
            assertEquals(expected.text, result[index].text)
        }
    }

    @Test
    fun whenCurrenciesEmpty_thenGotEmptyCurrencyUiModel() = runBlocking {
        currencyRepository.sendCurrenciesModel(emptyList())

        assertEquals(0, useCase().first().size)
    }
}

val currenciesInput = listOf(
    CurrencyModel("AED", "United Arab Emirates Dirham"),
    CurrencyModel("AFN", "Afghan Afghani"),
    CurrencyModel("ALL", "Albanian Lek"),
)

val currenciesExpected = listOf(
    CurrencyUiModel("AED", "AED - United Arab Emirates Dirham"),
    CurrencyUiModel("AFN", "AFN - Afghan Afghani"),
    CurrencyUiModel("ALL", "ALL - Albanian Lek"),
)
