package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration45 : Migration(42, 43) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE TimetableAdditional ADD COLUMN is_added_by_user INTEGER NOT NULL DEFAULT 0")
    }
}
