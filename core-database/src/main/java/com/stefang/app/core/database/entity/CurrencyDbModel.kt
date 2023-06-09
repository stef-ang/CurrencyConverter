package com.stefang.app.core.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currency")
data class CurrencyDbModel(
    @PrimaryKey val code: String,
    val name: String
)
