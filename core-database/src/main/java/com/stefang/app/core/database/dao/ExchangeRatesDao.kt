package com.stefang.app.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.stefang.app.core.database.entity.ExchangeRatesBdModel
import kotlinx.coroutines.flow.Flow

@Dao
interface ExchangeRatesDao {

    @Query("SELECT * FROM exchange_rate WHERE base = :base ORDER BY code asc")
    fun getAllByBase(base: String): Flow<List<ExchangeRatesBdModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(exchangeRates: List<ExchangeRatesBdModel>)
}
