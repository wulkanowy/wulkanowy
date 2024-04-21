package io.github.wulkanowy.data.db.migrations

import androidx.room.migration.AutoMigrationSpec
import androidx.sqlite.db.SupportSQLiteDatabase

class Migration63 : AutoMigrationSpec {

    override fun onPostMigrate(db: SupportSQLiteDatabase) {
        db.execSQL("DROP TABLE IF EXISTS `Semesters`")
        db.execSQL("DROP TABLE IF EXISTS `School`")
        db.execSQL("DROP TABLE IF EXISTS `Teachers`")

        db.execSQL("CREATE TABLE IF NOT EXISTS `Semesters` (`student_id` INTEGER NOT NULL, `diary_id` INTEGER NOT NULL, `kindergarten_diary_id` INTEGER NOT NULL DEFAULT 0, `diary_name` TEXT NOT NULL, `school_year` INTEGER NOT NULL, `semester_id` INTEGER NOT NULL, `semester_name` INTEGER NOT NULL, `start` INTEGER NOT NULL, `end` INTEGER NOT NULL, `class_id` INTEGER NOT NULL, `unit_id` INTEGER NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `is_current` INTEGER NOT NULL)")
        db.execSQL("CREATE UNIQUE INDEX IF NOT EXISTS `index_Semesters_student_id_diary_id_kindergarten_diary_id_semester_id` ON `Semesters` (`student_id`, `diary_id`, `kindergarten_diary_id`, `semester_id`)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `School` (`student_id` INTEGER NOT NULL, `class_id` INTEGER NOT NULL, `name` TEXT NOT NULL, `address` TEXT NOT NULL, `contact` TEXT NOT NULL, `headmaster` TEXT NOT NULL, `pedagogue` TEXT NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")
        db.execSQL("CREATE TABLE IF NOT EXISTS `Teachers` (`student_id` INTEGER NOT NULL, `class_id` INTEGER NOT NULL, `subject` TEXT NOT NULL, `name` TEXT NOT NULL, `short_name` TEXT NOT NULL, `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL)")

        db.execSQL("UPDATE Students SET is_edu_one = NULL")
    }
}
