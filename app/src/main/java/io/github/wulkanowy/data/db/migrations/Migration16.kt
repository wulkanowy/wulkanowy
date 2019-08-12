package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration16 : Migration(15, 16) {

    override fun migrate(database: SupportSQLiteDatabase) {
        migrateMessages(database)
        migrateGrades(database)
        migrateStudents(database)
    }

    private fun migrateMessages(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE Messages")
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS Messages (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                is_notified INTEGER NOT NULL,
                student_id INTEGER NOT NULL,
                real_id INTEGER NOT NULL,
                message_id INTEGER NOT NULL,
                sender_name TEXT NOT NULL,
                sender_id INTEGER NOT NULL,
                recipient_name TEXT NOT NULL,
                subject TEXT NOT NULL,
                content TEXT NOT NULL,
                date INTEGER NOT NULL,
                folder_id INTEGER NOT NULL,
                unread INTEGER NOT NULL,
                unread_by INTEGER NOT NULL,
                read_by INTEGER NOT NULL,
                removed INTEGER NOT NULL
            )
        """)
    }

    private fun migrateGrades(database: SupportSQLiteDatabase) {
        database.execSQL("DROP TABLE Grades")
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS Grades (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                is_read INTEGER NOT NULL,
                is_notified INTEGER NOT NULL,
                semester_id INTEGER NOT NULL,
                student_id INTEGER NOT NULL,
                subject TEXT NOT NULL,
                entry TEXT NOT NULL,
                value REAL NOT NULL,
                modifier REAL NOT NULL,
                comment TEXT NOT NULL,
                color TEXT NOT NULL,
                grade_symbol TEXT NOT NULL,
                description TEXT NOT NULL,
                weight TEXT NOT NULL,
                weightValue REAL NOT NULL,
                date INTEGER NOT NULL,
                teacher TEXT NOT NULL
            )
        """)
    }

    private fun migrateStudents(database: SupportSQLiteDatabase) {
        database.execSQL("""
            CREATE TABLE IF NOT EXISTS Students_tmp (
                id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                scrapperBaseUrl TEXT NOT NULL,
                apiBaseUrl TEXT NOT NULL,
                loginType TEXT NOT NULL,
                loginMode TEXT NOT NULL,
                certificateKey TEXT NOT NULL,
                certificate TEXT NOT NULL,
                apiKey TEXT NOT NULL,
                email TEXT NOT NULL,
                password TEXT NOT NULL,
                symbol TEXT NOT NULL,
                student_id INTEGER NOT NULL,
                user_login_id INTEGER NOT NULL,
                student_name TEXT NOT NULL,
                school_id TEXT NOT NULL,
                school_name TEXT NOT NULL,
                class_name TEXT NOT NULL,
                class_id INTEGER NOT NULL,
                is_current INTEGER NOT NULL,
                registration_date INTEGER NOT NULL
            )
        """)

        database.execSQL("ALTER TABLE Students ADD COLUMN scrapperBaseUrl TEXT NOT NULL DEFAULT \"\";")
        database.execSQL("ALTER TABLE Students ADD COLUMN apiBaseUrl TEXT NOT NULL DEFAULT \"\";")
        database.execSQL("ALTER TABLE Students ADD COLUMN loginMode TEXT NOT NULL DEFAULT \"\";")
        database.execSQL("ALTER TABLE Students ADD COLUMN certificateKey TEXT NOT NULL DEFAULT \"\";")
        database.execSQL("ALTER TABLE Students ADD COLUMN certificate TEXT NOT NULL DEFAULT \"\";")
        database.execSQL("ALTER TABLE Students ADD COLUMN apiKey TEXT NOT NULL DEFAULT \"\";")
        database.execSQL("ALTER TABLE Students ADD COLUMN user_login_id INTEGER NOT NULL DEFAULT 0;")

        database.execSQL("""
            INSERT INTO Students_tmp(
            id, scrapperBaseUrl, apiBaseUrl, loginType, loginMode, certificateKey, certificate, apiKey, email, password, symbol, student_id, user_login_id, student_name, school_id, school_name, school_id, school_name, class_name, class_id, is_current, registration_date)
            SELECT
            id, endpoint, apiBaseUrl, loginType, "SCRAPPER", certificateKey, certificate, apiKey, email, password, symbol, student_id, user_login_id, student_name, school_id, school_name, school_id, school_name, class_name, class_id, is_current, registration_date
            FROM Students
        """)
        database.execSQL("DROP TABLE Students")
        database.execSQL("ALTER TABLE Students_tmp RENAME TO Students")
        database.execSQL("CREATE UNIQUE INDEX index_Students_email_symbol_student_id_school_id_class_id ON Students (email, symbol, student_id, school_id, class_id)")
    }
}
