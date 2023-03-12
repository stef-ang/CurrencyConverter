package com.stefang.app.core.database.entity

import androidx.room.Entity

@Entity(tableName = "exchange_rate", primaryKeys = ["code", "base"])
data class ExchangeRatesBdModel(
    val code: String,
    val base: String,
    val rate: Double
)
