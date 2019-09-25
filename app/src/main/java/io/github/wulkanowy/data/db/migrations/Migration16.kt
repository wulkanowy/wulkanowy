package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration16 : Migration(15, 16) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS GradesPointsStatistics(
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                student_id INTEGER NOT NULL,
                semester_id INTEGER NOT NULL,
                subject TEXT NOT NULL,
                others REAL NOT NULL,
                student REAL NOT NULL
            )
        """)
    }
}
