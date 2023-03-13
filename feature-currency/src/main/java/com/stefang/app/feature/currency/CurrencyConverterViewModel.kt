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
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
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

    private val snackBarEventSharedFlow = MutableSharedFlow<SnackBarEvent>()
    val snackBarEvent: SharedFlow<SnackBarEvent> = snackBarEventSharedFlow

    init {
        viewModelScope.launch {
            try {
                currencyRepository.tryUpdateCurrenciesAndRates()
            } catch (e: Exception) {
                snackBarEventSharedFlow.emit(SnackBarEvent.NetworkError)
                Log.e("CurrencyConverter", ": ${e.message}")
            }
        }
    }

    val allCurrencies: StateFlow<List<CurrencyUiModel>> = getAllCurrenciesUseCase().catch {
        emit(emptyList())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    @OptIn(ExperimentalCoroutinesApi::class)
    val allExchangeResults: StateFlow<List<ExchangeResultUiModel>> =
        sourceCurrencyFlow.combine(amountFlow) { source, amount ->
            return@combine Pair(source, amount)
        }.filter {
            it.first != null && it.second > 0.0
        }.flatMapLatest {
            Log.d("allExchangeResults", "flatMapLatest $it")
            getAllExchangeResultsUseCase(it.first, it.second)
        }.catch {
            emit(emptyList())
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

    sealed class SnackBarEvent {
        object None : SnackBarEvent()
        object NetworkError : SnackBarEvent()
    }
}
