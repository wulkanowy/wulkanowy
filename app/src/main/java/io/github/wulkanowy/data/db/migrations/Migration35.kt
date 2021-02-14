package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.wulkanowy.utils.AppInfo

class Migration35(private val appInfo: AppInfo) : Migration(34, 35) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Students ADD COLUMN `avatar_color` INTEGER NOT NULL DEFAULT 0")

        val studentsCursor = database.query("SELECT * FROM Students")

        while (studentsCursor.moveToNext()) {
            val studentId = studentsCursor.getColumnIndexOrThrow("id")
            database.execSQL(
                """UPDATE Students 
                SET avatar_color = ${appInfo.defaultColorsForAvatar.random()} 
                WHERE id = $studentId"""
            )
        }
    }
}
