package io.github.wulkanowy.data.db.migrations

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import org.junit.Assert.assertEquals
import org.junit.Test

class Migration13Test : AbstractMigrationTest() {

    @Test
    fun studentsWithSchoolNameWithClassName() {
        helper.createDatabase(dbName, 12).apply {
            createStudent(this, 1, "Klasa A - Publiczna szkoła Wulkanowego nr 1 w fakelog.cf")
            createStudent(this, 2, "Klasa B - Publiczna szkoła Wulkanowego-fejka nr 1 w fakelog.cf")
            close()
        }

        helper.runMigrationsAndValidate(dbName, 13, true, Migration13())

        val db = getMigratedRoomDatabase()
        val students = db.studentDao.loadAll().blockingGet()

        assertEquals(2, students.size)

        students[0].run {
            assertEquals(1, studentId)
            assertEquals("Klasa A", className)
            assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", schoolName)
        }

        students[1].run {
            assertEquals(2, studentId)
            assertEquals("Klasa B", className)
            assertEquals("Publiczna szkoła Wulkanowego-fejka nr 1 w fakelog.cf", schoolName)
        }
    }

    @Test
    fun studentsWithSchoolNameWithoutClassName() {
        helper.createDatabase(dbName, 12).apply {
            createStudent(this, 1, "Publiczna szkoła Wulkanowego nr 1 w fakelog.cf")
            createStudent(this, 2, "Publiczna szkoła Wulkanowego-fejka nr 1 w fakelog.cf")
            close()
        }

        helper.runMigrationsAndValidate(dbName, 13, true, Migration13())

        val db = getMigratedRoomDatabase()
        val students = db.studentDao.loadAll().blockingGet()

        assertEquals(2, students.size)

        students[0].run {
            assertEquals(1, studentId)
            assertEquals("", className)
            assertEquals("Publiczna szkoła Wulkanowego nr 1 w fakelog.cf", schoolName)
        }

        students[1].run {
            assertEquals(2, studentId)
            assertEquals("", className)
            assertEquals("Publiczna szkoła Wulkanowego-fejka nr 1 w fakelog.cf", schoolName)
        }
    }

    private fun createStudent(db: SupportSQLiteDatabase, studentId: Int, schoolName: String = "", classId: Int = -1) {
        db.insert("Students", SQLiteDatabase.CONFLICT_FAIL, ContentValues().apply {
            put("endpoint", "https://fakelog.cf")
            put("loginType", "STANDARD")
            put("email", "jan@fakelog.cf")
            put("password", "******")
            put("symbol", "Default")
            put("student_id", studentId)
            put("class_id", classId)
            put("student_name", "Jan Kowalski")
            put("school_id", "000123")
            put("school_name", schoolName)
            put("is_current", false)
            put("registration_date", "0")
        })
    }
}
