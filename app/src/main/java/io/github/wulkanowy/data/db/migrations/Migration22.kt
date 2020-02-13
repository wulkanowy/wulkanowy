package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration22 : Migration(21, 22) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Exams ADD COLUMN calendarSync INTEGER NOT NULL DEFAULT 0")
    }
}