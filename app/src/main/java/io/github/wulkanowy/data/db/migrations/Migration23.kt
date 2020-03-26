package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration23 : Migration(22, 23) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS MessageAttachments (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                real_id INTEGER NOT NULL,
                message_id INTEGER NOT NULL,
                one_drive_id TEXT NOT NULL,
                url TEXT NOT NULL,
                filename TEXT NOT NULL
            )
        """)
    }
}
