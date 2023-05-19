package com.stefang.app.core.database

import androidx.room.RenameColumn
import androidx.room.migration.AutoMigrationSpec

object DatabaseMigrations {

    @RenameColumn(
        tableName = "history",
        fromColumnName = "rate",
        toColumnName = "amount"
    )
    @RenameColumn(
        tableName = "history",
        fromColumnName = "insertedAt",
        toColumnName = "created_at"
    )
    class Schema1to2: AutoMigrationSpec
}
