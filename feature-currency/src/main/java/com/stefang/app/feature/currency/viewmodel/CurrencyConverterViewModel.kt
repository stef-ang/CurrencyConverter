package com.stefang.app.feature.currency.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefang.app.core.data.log.Logger
import com.stefang.app.core.data.repository.CurrencyRepository
import com.stefang.app.core.data.repository.HistoryRepository
import com.stefang.app.feature.currency.model.CurrencyUiModel
import com.stefang.app.feature.currency.model.ExchangeResultUiModel
import com.stefang.app.feature.currency.usecase.GetAllCurrenciesUseCase
import com.stefang.app.feature.currency.usecase.GetAllExchangeResultsUseCase
import com.stefang.app.feature.currency.utils.AppDispatchers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CurrencyConverterViewModel @Inject constructor(
    private val currencyRepository: CurrencyRepository,
    getAllCurrenciesUseCase: GetAllCurrenciesUseCase,
    getAllExchangeResultsUseCase: GetAllExchangeResultsUseCase,
    private val logger: Logger,
    private val historyRepository: HistoryRepository,
    private val appDispatchers: AppDispatchers
) : ViewModel() {

    private var job: Job? = null

    private val amountFlow = MutableStateFlow(0)
    val amountState: StateFlow<Int> = amountFlow

    private val sourceCurrencyFlow = MutableStateFlow<CurrencyUiModel?>(null)
    val sourceCurrencyState: StateFlow<CurrencyUiModel?> = sourceCurrencyFlow

    private val snackBarEventSharedFlow = MutableSharedFlow<SnackBarEvent>()
    val snackBarEvent: SharedFlow<SnackBarEvent> = snackBarEventSharedFlow

    private val trackHistorySharedFlow = MutableSharedFlow<Pair<String, Int>>()
    val trackHistoryEvent: SharedFlow<Pair<String, Int>> = trackHistorySharedFlow

    private fun fetchCurrenciesAndRates() {
        viewModelScope.launch {
            try {
                currencyRepository.tryUpdateCurrenciesAndRates()
            } catch (e: Exception) {
                snackBarEventSharedFlow.emit(SnackBarEvent.NetworkError)
                logger.error(e)
            }
        }
    }

    val allCurrencies: StateFlow<List<CurrencyUiModel>> = getAllCurrenciesUseCase().catch {
        snackBarEventSharedFlow.emit(SnackBarEvent.ComputationError)
        emit(emptyList())
    }.onStart {
        fetchCurrenciesAndRates()
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
            it.first != null && it.second > 0
        }.flatMapLatest {
            it.first?.let { source -> trackHistorySharedFlow.emit(source.code to it.second) }
            getAllExchangeResultsUseCase(it.first?.code, it.second.toDouble())
        }.catch {
            snackBarEventSharedFlow.emit(SnackBarEvent.ComputationError)
            emit(emptyList())
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = emptyList()
        )

    fun updateAmount(amount: String) {
        amount.toIntOrNull()?.let { amountFlow.value = it }
    }

    fun updateSourceCurrency(currency: CurrencyUiModel) {
        sourceCurrencyFlow.value = currency
    }

    fun trackHistory(code: String, amount: Int) {
        job?.cancel()
        job = viewModelScope.launch(appDispatchers.io) {
            // delay for 2 seconds before track the history
            delay(TRACKER_DELAY)
            historyRepository.trackHistory(code, amount, System.currentTimeMillis())
        }
    }

    sealed interface SnackBarEvent {
        object NetworkError : SnackBarEvent
        object ComputationError : SnackBarEvent
    }

    companion object {
        private const val TRACKER_DELAY = 2000L
    }
}
