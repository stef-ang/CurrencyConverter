package com.stefang.app.core.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("history")
data class HistoryDbModel(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val code: String,
    val name: String,
    val amount: Int,
    @ColumnInfo(name = "created_at") val createdAt: Long
) {
    constructor(
        code: String,
        name: String,
        amount: Int,
        insertedAt: Long
    ) : this(0, code, name, amount, insertedAt)
}
