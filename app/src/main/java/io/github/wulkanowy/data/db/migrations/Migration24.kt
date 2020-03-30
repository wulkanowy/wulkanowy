package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration24 : Migration(23, 24) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Homework ADD COLUMN is_done INTEGER NOT NULL DEFAULT 0")
    }
}
