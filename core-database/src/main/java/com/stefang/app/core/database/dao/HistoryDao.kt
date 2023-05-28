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
    suspend fun insertHistory(history: HistoryDbModel)

    @Query("DELETE FROM history WHERE id = :id")
    suspend fun deleteById(id: Int)

    @Query("DELETE FROM history")
    suspend fun deleteAll()
}
