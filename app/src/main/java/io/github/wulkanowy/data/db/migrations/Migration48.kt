package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import io.github.wulkanowy.data.db.Converters
import io.github.wulkanowy.ui.modules.Destination

class Migration48 : Migration(47, 48) {

    override fun migrate(database: SupportSQLiteDatabase) {
        val defaultDestination = Converters().destinationToString(Destination.Dashboard)
        database.execSQL(
            "ALTER TABLE Notifications ADD COLUMN destination TEXT NOT NULL DEFAULT `${defaultDestination}`",
        )
    }
}