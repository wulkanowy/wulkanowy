package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime

@Entity(tableName = "Timetable")
data class Timetable(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "diary_id")
    val diaryId: Int,

    val number: Int,

    @Deprecated("use startZoned instead")
    val start: LocalDateTime,

    @Deprecated("use endZoned instead")
    val end: LocalDateTime,

    @ColumnInfo(name = "start_zoned", defaultValue = "0")
    val startZoned: ZonedDateTime,

    @ColumnInfo(name = "end_zoned", defaultValue = "0")
    val endZoned: ZonedDateTime,

    val date: LocalDate,

    val subject: String,

    val subjectOld: String,

    val group: String,

    val room: String,

    val roomOld: String,

    val teacher: String,

    val teacherOld: String,

    val info: String,

    @ColumnInfo(name = "student_plan")
    val isStudentPlan: Boolean,

    val changes: Boolean,

    val canceled: Boolean
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "is_notified")
    var isNotified: Boolean = true
}
