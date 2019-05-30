package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration14 : Migration(13, 14) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Attendance ADD COLUMN excusable INTEGER DEFAULT 0 NOT NULL")
    }
}
