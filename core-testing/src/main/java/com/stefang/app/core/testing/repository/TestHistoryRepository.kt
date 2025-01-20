package com.stefang.app.core.testing.repository

import com.stefang.app.core.data.model.HistoryModel
import com.stefang.app.core.data.repository.HistoryRepository
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class TestHistoryRepository : HistoryRepository {

    private val historiesFlow: MutableSharedFlow<List<HistoryModel>> =
        MutableSharedFlow(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override val histories: Flow<List<HistoryModel>>
        get() = historiesFlow

    override suspend fun trackHistory(code: String, amount: Int, trackerTime: Long) {
        historiesFlow.tryEmit(
            listOf(
                HistoryModel(
                    id = 1,
                    code = code,
                    name = "Currency",
                    amount = amount
                )
            )
        )
    }

    var hasTriedDeleteHistory = false
    override suspend fun deleteHistory(id: Int) { hasTriedDeleteHistory = true }

    var hasTriedDeleteAllHistory = false
    override suspend fun deleteAllHistory() { hasTriedDeleteAllHistory = true }

    fun sendHistories(histories: List<HistoryModel>) {
        historiesFlow.tryEmit(histories)
    }

    companion object {

        val histories = listOf(
            HistoryModel(
                id = 1,
                code = "USD",
                name = "United States Dollar",
                amount = 100
            ),
            HistoryModel(
                id = 2,
                code = "IDR",
                name = "Indonesian Rupiah",
                amount = 1000000
            ),
            HistoryModel(
                id = 3,
                code = "EUR",
                name = "Euro",
                amount = 100
            ),
            HistoryModel(
                id = 4,
                code = "JPY",
                name = "Japanese Yen",
                amount = 100
            )
        )
    }
}
