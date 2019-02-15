package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration4 : Migration(3, 4) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("CREATE TABLE ReportingUnits (" +
            "id INTEGER NOT NULL PRIMARY KEY," +
            "student_id INTEGER NOT NULL," +
            "real_id INTEGER NOT NULL," +
            "short TEXT NOT NULL," +
            "sender_id INTEGER NOT NULL," +
            "sender_name TEXT NOT NULL," +
            "roles TEXT NOT NULL)")

        database.execSQL("CREATE TABLE Recipients (" +
            "id INTEGER NOT NULL PRIMARY KEY," +
            "student_id INTEGER NOT NULL," +
            "real_id TEXT NOT NULL," +
            "name TEXT NOT NULL," +
            "login_id INTEGER NOT NULL," +
            "unit_id INTEGER NOT NULL," +
            "role INTEGER NOT NULL," +
            "hash TEXT NOT NULL)")
    }
}
