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

    override suspend fun trackHistory(code: String, amount: Int, trackerTime: Long) {}
}
