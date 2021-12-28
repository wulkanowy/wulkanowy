package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.wulkanowy.utils.toLocalDateTime
import java.time.ZoneId

class Migration46 : Migration(45, 46) {

    override fun migrate(database: SupportSQLiteDatabase) {
        migrateConferences(database)
        migrateMessages(database)
        migrateMobileDevices(database)
        migrateNotifications(database)
        migrateTimetable(database)
        migrateTimetableAdditional(database)
    }

    private fun migrateConferences(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Conferences ADD COLUMN date_zoned INTEGER NOT NULL DEFAULT 0")

        val cursor = database.query("SELECT * FROM Conferences")
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            val timestampLocal = cursor.getLong(cursor.getColumnIndexOrThrow("date"))
            val timestampUtc = timestampLocal.timestampLocalToUTC()

            database.execSQL("UPDATE Conferences SET date_zoned = $timestampUtc WHERE id = $id")
        }
    }

    private fun migrateMessages(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Messages ADD COLUMN date_zoned INTEGER NOT NULL DEFAULT 0")

        val cursor = database.query("SELECT * FROM Messages")
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            val timestampLocal = cursor.getLong(cursor.getColumnIndexOrThrow("date"))
            val timestampUtc = timestampLocal.timestampLocalToUTC()

            database.execSQL("UPDATE Messages SET date_zoned = $timestampUtc WHERE id = $id")
        }
    }

    private fun migrateMobileDevices(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE MobileDevices ADD COLUMN date_zoned INTEGER NOT NULL DEFAULT 0")

        val cursor = database.query("SELECT * FROM MobileDevices")
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            val timestampLocal = cursor.getLong(cursor.getColumnIndexOrThrow("date"))
            val timestampUtc = timestampLocal.timestampLocalToUTC()

            database.execSQL("UPDATE MobileDevices SET date_zoned = $timestampUtc WHERE id = $id")
        }
    }

    private fun migrateNotifications(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Notifications ADD COLUMN date_zoned INTEGER NOT NULL DEFAULT 0")

        val cursor = database.query("SELECT * FROM Notifications")
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            val timestampLocal = cursor.getLong(cursor.getColumnIndexOrThrow("date"))
            val timestampUtc = timestampLocal.timestampLocalToUTC()

            database.execSQL("UPDATE Notifications SET date_zoned = $timestampUtc WHERE id = $id")
        }
    }

    private fun migrateTimetable(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Timetable ADD COLUMN start_zoned INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE Timetable ADD COLUMN end_zoned INTEGER NOT NULL DEFAULT 0")

        val cursor = database.query("SELECT * FROM Timetable")
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            val timestampLocalStart = cursor.getLong(cursor.getColumnIndexOrThrow("start"))
            val timestampLocalEnd = cursor.getLong(cursor.getColumnIndexOrThrow("end"))
            val timestampUtcStart = timestampLocalStart.timestampLocalToUTC()
            val timestampUtcEnd = timestampLocalEnd.timestampLocalToUTC()

            database.execSQL("UPDATE Timetable SET start_zoned = $timestampUtcStart, end_zoned = $timestampUtcEnd WHERE id = $id")
        }
    }

    private fun migrateTimetableAdditional(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE TimetableAdditional ADD COLUMN start_zoned INTEGER NOT NULL DEFAULT 0")
        database.execSQL("ALTER TABLE TimetableAdditional ADD COLUMN end_zoned INTEGER NOT NULL DEFAULT 0")

        val cursor = database.query("SELECT * FROM TimetableAdditional")
        while (cursor.moveToNext()) {
            val id = cursor.getLong(cursor.getColumnIndexOrThrow("id"))
            val timestampLocalStart = cursor.getLong(cursor.getColumnIndexOrThrow("start"))
            val timestampLocalEnd = cursor.getLong(cursor.getColumnIndexOrThrow("end"))
            val timestampUtcStart = timestampLocalStart.timestampLocalToUTC()
            val timestampUtcEnd = timestampLocalEnd.timestampLocalToUTC()

            database.execSQL("UPDATE TimetableAdditional SET start_zoned = $timestampUtcStart, end_zoned = $timestampUtcEnd WHERE id = $id")
        }
    }

    private fun Long.timestampLocalToUTC(): Long =
        toLocalDateTime().atZone(ZoneId.of("Europe/Warsaw")).toInstant().toEpochMilli()
}
