package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration22 : Migration(21, 22) {

    override fun migrate(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE Semesters")
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS Semesters (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                student_id INTEGER NOT NULL,
                diary_id INTEGER NOT NULL,
                diary_name TEXT NOT NULL,
                school_year INTEGER NOT NULL,
                semester_id INTEGER NOT NULL,
                semester_name INTEGER NOT NULL,
                start INTEGER NOT NULL,
                `end` INTEGER NOT NULL,
                class_id INTEGER NOT NULL,
                unit_id INTEGER NOT NULL
            )
        """)
        database.execSQL("CREATE UNIQUE INDEX index_Semesters_student_id_diary_id_semester_id ON Semesters (student_id, diary_id, semester_id)")
    }
}
