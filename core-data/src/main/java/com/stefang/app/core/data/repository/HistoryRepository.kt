package com.stefang.app.core.data.repository

import com.stefang.app.core.data.model.HistoryModel
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {

    val histories: Flow<List<HistoryModel>>

    suspend fun trackHistory(code: String, amount: Int, trackerTime: Long)

    suspend fun deleteHistory(id: Int)
}
