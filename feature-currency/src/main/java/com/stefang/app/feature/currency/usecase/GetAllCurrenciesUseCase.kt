package com.stefang.app.feature.currency.usecase

import com.stefang.app.core.data.repository.CurrencyRepository
import com.stefang.app.feature.currency.model.CurrencyUiModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllCurrenciesUseCase @Inject constructor(
    private val currencyRepository: CurrencyRepository
) {

    operator fun invoke(): Flow<List<CurrencyUiModel>> = currencyRepository.currencies.map { list ->
        list.map {
            CurrencyUiModel(
                code = it.code,
                text = "${it.code} - ${it.name}"
            )
        }
    }
}
