package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration14 : Migration(13, 14) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS MobileDevices (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                student_id INTEGER NOT NULL,
                column_id INTEGER NOT NULL,
                name TEXT NOT NULL,
                date INTEGER NOT NULL
            )
        """)
    }
}
