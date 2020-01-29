package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration22 : Migration(21, 22) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE AttendanceSummary")
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS `AttendanceSummary` (
                `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                `student_id` INTEGER NOT NULL,
                `diary_id` INTEGER NOT NULL,
                `subject_id` INTEGER NOT NULL,
                `month` INTEGER,
                `presence` INTEGER NOT NULL,
                `absence` INTEGER NOT NULL,
                `absence_excused` INTEGER NOT NULL,
                `absence_for_school_reasons` INTEGER NOT NULL,
                `lateness` INTEGER NOT NULL,
                `lateness_excused` INTEGER NOT NULL,
                `exemption` INTEGER NOT NULL
            )
        """)
    }
}