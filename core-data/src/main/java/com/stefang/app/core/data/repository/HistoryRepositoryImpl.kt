package com.stefang.app.core.data.repository

import com.stefang.app.core.data.mapper.toModel
import com.stefang.app.core.data.model.HistoryModel
import com.stefang.app.core.database.datasource.CurrencyLocalDataSource
import com.stefang.app.core.database.datasource.HistoryDataSource
import com.stefang.app.core.database.entity.HistoryDbModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val historyDataSource: HistoryDataSource,
    private val currencyDataSource: CurrencyLocalDataSource
) : HistoryRepository {

    override val histories: Flow<List<HistoryModel>>
        get() = historyDataSource.getAllHistories().map { list ->
            list.map { it.toModel() }
        }

    override suspend fun trackHistory(
        code: String,
        amount: Int,
        trackerTime: Long
    ): Unit = withContext(Dispatchers.IO) {
        currencyDataSource.getCurrency(code)?.name?.let { currencyName ->
            historyDataSource.insertHistory(
                HistoryDbModel(code, currencyName, amount, trackerTime)
            )
        }
    }

    override suspend fun deleteHistory(id: Int): Unit = withContext(Dispatchers.IO) {
        historyDataSource.deleteHistory(id)
    }
}
