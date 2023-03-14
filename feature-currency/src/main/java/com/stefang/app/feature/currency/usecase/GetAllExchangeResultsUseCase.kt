package com.stefang.app.feature.currency.usecase

import com.stefang.app.core.data.CurrencyRepository
import com.stefang.app.feature.currency.model.ExchangeResultUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.DecimalFormat
import javax.inject.Inject

class GetAllExchangeResultsUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository
) {

    private val numberFormat = DecimalFormat("#,###.##")

    operator fun invoke(source: String?, amount: Double): Flow<List<ExchangeResultUiModel>> {
        return currencyRepository.exchangeRates.map { rates ->
            rates.firstOrNull { it.code == source }?.let { sourceCurrency ->
                val rateSource = sourceCurrency.rate
                // convert from source to base
                val inBase = amount / rateSource
                rates.map {
                    // convert from base to all currencies
                    val rateResult = it.rate * inBase
                    ExchangeResultUiModel(
                        code = it.code,
                        amount = numberFormat.format(rateResult)
                    )
                }
            } ?: emptyList()
        }
    }
}
