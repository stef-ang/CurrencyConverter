package com.stefang.app.core.database.datasource

import com.stefang.app.core.database.entity.HistoryDbModel
import kotlinx.coroutines.flow.Flow

interface HistoryDataSource {

    fun getAllHistories(): Flow<List<HistoryDbModel>>

    suspend fun insertHistory(history: HistoryDbModel)

    suspend fun deleteHistory(id: Int)

    suspend fun deleteAllHistory()
}
