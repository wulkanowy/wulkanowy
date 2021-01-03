package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration31 : Migration(30, 31) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS Preferences (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                studentId INTEGER NOT NULL,
                `key` TEXT NOT NULL,
                value TEXT NOT NULL
            )
        """)
        database.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS index_Preferences_studentId_key ON Preferences (studentId, `key`)")
    }
}
