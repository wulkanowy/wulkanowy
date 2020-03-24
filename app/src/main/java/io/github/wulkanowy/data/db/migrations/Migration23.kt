package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration23 : Migration(22, 23) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Exams ADD COLUMN calendarSync INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE Homework ADD COLUMN calendarSync INTEGER NOT NULL DEFAULT 0")
    }
}
