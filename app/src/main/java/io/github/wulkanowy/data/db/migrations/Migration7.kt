package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration7 : Migration(6, 7) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE Messages")
        database.execSQL("""
            CREATE TABLE Messages (
                id INTEGER PRIMARY KEY NOT NULL,
                student_id INTEGER NOT NULL,
                real_id INTEGER NOT NULL,
                message_id INTEGER NOT NULL,
                sender_name TEXT NOT NULL,
                sender_id INTEGER NOT NULL,
                recipient_name TEXT NOT NULL,
                subject TEXT NOT NULL,
                date INTEGER NOT NULL,
                folder_id INTEGER NOT NULL,
                unread INTEGER NOT NULL,
                unread_by INTEGER NOT NULL,
                read_by INTEGER NOT NULL,
                removed INTEGER NOT NULL,
                is_notified INTEGER NOT NULL,
                content TEXT)
            """)
    }
}
