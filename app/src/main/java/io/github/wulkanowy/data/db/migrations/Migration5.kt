package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration5 : Migration(4, 5) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE IF NOT EXISTS `GradesStatistics` (" +
            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," +
            "`student_id` INTEGER NOT NULL," +
            "`semester_id` INTEGER NOT NULL," +
            "`subject` TEXT NOT NULL," +
            "`grade` INTEGER NOT NULL," +
            "`amount` INTEGER NOT NULL," +
            "`annual` INTEGER NOT NULL)")
    }
}
