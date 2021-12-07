package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.wulkanowy.data.db.Converters
import io.github.wulkanowy.ui.modules.Destination

class Migration45 : Migration(44, 45) {
    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            "ALTER TABLE Notifications ADD COLUMN destination TEXT NOT NULL DEFAULT `${
                Converters().destinationToString(Destination.Dashboard)
            }`",
        )
    }
}