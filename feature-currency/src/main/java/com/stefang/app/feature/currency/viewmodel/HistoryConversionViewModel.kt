package com.stefang.app.feature.currency.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefang.app.core.data.log.Logger
import com.stefang.app.core.data.repository.HistoryRepository
import com.stefang.app.feature.currency.model.HistoryConversionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HistoryConversionViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val logger: Logger
): ViewModel() {

    private val snackBarEventSharedFlow = MutableSharedFlow<SnackBarEvent>()
    val snackBarEvent: SharedFlow<SnackBarEvent> = snackBarEventSharedFlow

    private val showBottomSheetSharedFlow = MutableSharedFlow<Boolean>()
    val showBottomSheetEvent: SharedFlow<Boolean> = showBottomSheetSharedFlow

    val allHistories: StateFlow<List<HistoryConversionUiModel>> = historyRepository.histories.catch {
        emit(emptyList())
    }.map { list ->
        list.map { HistoryConversionUiModel(it.id, it.amount, it.code, it.name) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )

    fun deleteHistory(id: Int) = viewModelScope.launch {
        try {
            historyRepository.deleteHistory(id)
            snackBarEventSharedFlow.emit(SnackBarEvent.DeleteSingle)
        } catch (e: Exception) {
            logger.error(e)
        }
    }

    fun deleteAllHistory() = viewModelScope.launch {
        try {
            historyRepository.deleteAllHistory()
            showBottomSheetSharedFlow.emit(false)
            snackBarEventSharedFlow.emit(SnackBarEvent.DeleteAll)
        } catch (e: Exception) {
            logger.error(e)
        }
    }

    sealed interface SnackBarEvent {
        object DeleteSingle: SnackBarEvent
        object DeleteAll: SnackBarEvent
    }
}
