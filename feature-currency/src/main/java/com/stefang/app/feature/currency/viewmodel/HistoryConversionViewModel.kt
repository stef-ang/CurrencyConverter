package com.stefang.app.feature.currency.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stefang.app.core.data.repository.HistoryRepository
import com.stefang.app.feature.currency.model.HistoryConversionUiModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistoryConversionViewModel @Inject constructor(
    historyRepository: HistoryRepository
): ViewModel() {

    val allHistories: StateFlow<List<HistoryConversionUiModel>> = historyRepository.histories.catch {
        emit(emptyList())
    }.map { list ->
        list.map { HistoryConversionUiModel(it.amount, it.code, it.name) }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = emptyList()
    )
}
