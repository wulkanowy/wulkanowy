package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.wulkanowy.utils.AppInfo

class Migration30(private val appInfo: AppInfo) : Migration(29, 30) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Students ADD COLUMN avatar_color TEXT NOT NULL DEFAULT '${appInfo.defaultColorsForAvatar.random()}'")
        database.execSQL("ALTER TABLE Students ADD COLUMN nick TEXT DEFAULT NULL")
    }
}
