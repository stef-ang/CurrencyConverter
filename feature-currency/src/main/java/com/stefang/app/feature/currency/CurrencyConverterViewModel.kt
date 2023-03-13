package com.stefang.app.feature.currency

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefang.app.core.data.CurrencyRepository
import com.stefang.app.feature.currency.model.CurrencyUiModel
import com.stefang.app.feature.currency.model.ExchangeResultUiModel
import com.stefang.app.feature.currency.usecase.GetAllCurrenciesUseCase
import com.stefang.app.feature.currency.usecase.GetAllExchangeResultsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyConverterViewModel @Inject constructor(
    currencyRepository: CurrencyRepository,
    getAllCurrenciesUseCase: GetAllCurrenciesUseCase,
    getAllExchangeResultsUseCase: GetAllExchangeResultsUseCase
) : ViewModel() {

    private val amountFlow = MutableStateFlow(0.0)
    private val sourceCurrencyFlow = MutableStateFlow<String?>(null)

    init {
        viewModelScope.launch {
            currencyRepository.tryUpdateCurrenciesAndRates()
        }
    }

    val allCurrencies: StateFlow<List<CurrencyUiModel>> = getAllCurrenciesUseCase().stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val allExchangeResults: StateFlow<List<ExchangeResultUiModel>> =
        sourceCurrencyFlow.combine(amountFlow) { source, amount ->
            Log.d("allExchangeResults", "$source $amount")
            return@combine Pair(source, amount)
        }.filter {
            it.first != null && it.second > 0.0
        }.flatMapLatest {
            Log.d("allExchangeResults", "flatMapLatest $it")
            getAllExchangeResultsUseCase(it.first, it.second)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun updateAmount(amount: String) {
        amount.toDoubleOrNull()?.let { amountFlow.value = it }
    }

    fun updateSourceCurrency(code: String) {
        sourceCurrencyFlow.value = code
    }
}
