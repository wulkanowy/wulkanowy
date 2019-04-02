package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration13 : Migration(12, 13) {

    override fun migrate(database: SupportSQLiteDatabase) {
        addClassNameToStudents(database)
        markAtLeastAndOnlyOneSemesterAtCurrent(database, getStudentsIds(database))
    }

    private fun addClassNameToStudents(database: SupportSQLiteDatabase) {
        database.execSQL("ALTER TABLE Students ADD COLUMN class_name TEXT DEFAULT \"\" NOT NULL")
        database.execSQL("UPDATE Students SET class_name = SUBSTR(school_name, 1, INSTR(school_name, ' - ') - 1)")
        database.execSQL("UPDATE Students SET school_name = SUBSTR(school_name, INSTR(school_name, ' - ') + 3) WHERE school_name LIKE 'Klasa %'")
    }

    private fun getStudentsIds(database: SupportSQLiteDatabase): List<Pair<Int, Int>> {
        val students = mutableListOf<Pair<Int, Int>>()
        val studentsCursor = database.query("SELECT student_id, class_id FROM Students")
        if (studentsCursor.moveToFirst()) {
            do {
                students.add(studentsCursor.getInt(0) to studentsCursor.getInt(1))
            } while (studentsCursor.moveToNext())
        }
        return students
    }

    private fun markAtLeastAndOnlyOneSemesterAtCurrent(database: SupportSQLiteDatabase, students: List<Pair<Int, Int>>) {
        students.forEach { (studentId, classId) ->
            database.execSQL("UPDATE Semesters SET is_current = 0 WHERE student_id = $studentId AND class_id = $classId")
            database.execSQL("UPDATE Semesters SET is_current = 1 WHERE id = (SELECT id FROM Semesters WHERE student_id = $studentId AND class_id = $classId ORDER BY semester_id DESC)")
        }
    }
}
