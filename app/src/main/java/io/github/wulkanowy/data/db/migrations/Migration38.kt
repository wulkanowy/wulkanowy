package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration38 : Migration(37, 38) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL(
            """
                ALTER TABLE Students ADD COLUMN `number` INTEGER NOT NULL DEFAULT 0
                """
        )
    }
}
