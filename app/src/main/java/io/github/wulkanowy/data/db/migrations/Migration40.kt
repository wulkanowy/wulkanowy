package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration40 : Migration(39, 40) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Homework ADD COLUMN is_added_by_user INTEGER NOT NULL DEFAULT 1")
    }
}