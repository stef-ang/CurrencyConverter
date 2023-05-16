package com.stefang.app.core.database.datasource

import com.stefang.app.core.database.dao.HistoryDao
import com.stefang.app.core.database.entity.HistoryDbModel
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class HistoryDataSourceImpl @Inject constructor(
    private val historyDao: HistoryDao
) : HistoryDataSource {

    override fun getAllHistories(): Flow<List<HistoryDbModel>> {
        return historyDao.getAllHistories()
    }

    override fun insertHistory(history: HistoryDbModel) {
        historyDao.insertHistory(history)
    }
}
