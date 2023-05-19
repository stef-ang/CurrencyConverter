package com.stefang.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.stefang.app.core.database.entity.HistoryDbModel
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history ORDER BY created_at DESC")
    fun getAllHistories(): Flow<List<HistoryDbModel>>

    @Insert
    fun insertHistory(history: HistoryDbModel)
}
