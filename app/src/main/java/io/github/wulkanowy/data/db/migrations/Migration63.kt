package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration63 : Migration(62, 63) {

    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("UPDATE Students SET is_edu_one = 'NULL'")
    }
}
