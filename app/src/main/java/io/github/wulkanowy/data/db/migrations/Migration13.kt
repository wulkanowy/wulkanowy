package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration13 : Migration(12, 13) {

    override fun migrate(database: SupportSQLiteDatabase) {
        addClassNameToStudents(database)
    }

    private fun addClassNameToStudents(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Students ADD COLUMN class_name TEXT DEFAULT \"\" NOT NULL")
        database.execSQL("UPDATE Students SET class_name = SUBSTR(school_name, 1, INSTR(school_name, ' - ') - 1)")
        database.execSQL("UPDATE Students SET school_name = SUBSTR(school_name, INSTR(school_name, ' - ') + 3) WHERE school_name LIKE 'Klasa %'")
    }
}
