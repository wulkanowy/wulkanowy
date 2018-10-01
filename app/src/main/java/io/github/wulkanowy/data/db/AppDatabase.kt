package io.github.wulkanowy.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import io.github.wulkanowy.data.db.dao.*
import io.github.wulkanowy.data.db.entities.*
import javax.inject.Singleton

@Singleton
@Database(
        entities = [
            Student::class,
            Semester::class,
            Exam::class,
            Timetable::class,
            Attendance::class
        ],
        version = 1,
        exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun studentDao(): StudentDao

    abstract fun semesterDao(): SemesterDao

    abstract fun examsDao(): ExamDao

    abstract fun timetableDao(): TimetableDao

    abstract fun attendanceDao(): AttendanceDao
}
