package io.github.wulkanowy.data.db

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.TypeConverters
import io.github.wulkanowy.data.db.dao.AttendanceDao
import io.github.wulkanowy.data.db.dao.ExamDao
import android.content.Context
import io.github.wulkanowy.data.db.dao.GradeDao
import io.github.wulkanowy.data.db.dao.GradeSummaryDao
import io.github.wulkanowy.data.db.dao.SemesterDao
import io.github.wulkanowy.data.db.dao.StudentDao
import io.github.wulkanowy.data.db.entities.Attendance
import io.github.wulkanowy.data.db.entities.Exam
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.data.db.entities.GradeSummary
import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.utils.DATABASE_NAME
import javax.inject.Singleton

@Singleton
@Database(
        entities = [
            Student::class,
            Semester::class,
            Exam::class,
            Attendance::class,
            Grade::class,
            GradeSummary::class
        ],
        version = 1,
        exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        fun newInstance(context: Context): AppDatabase {
            return Room.databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
                    .build()
        }
    }

    abstract fun studentDao(): StudentDao

    abstract fun semesterDao(): SemesterDao

    abstract fun examsDao(): ExamDao

    abstract fun attendanceDao(): AttendanceDao

    abstract fun gradeDao(): GradeDao

    abstract fun gradeSummaryDao(): GradeSummaryDao
}
