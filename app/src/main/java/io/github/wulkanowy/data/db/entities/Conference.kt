package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZonedDateTime

@Entity(tableName = "Conferences")
data class Conference(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "diary_id")
    val diaryId: Int,

    val title: String,

    val subject: String,

    val agenda: String,

    @ColumnInfo(name = "present_on_conference")
    val presentOnConference: String,

    @ColumnInfo(name = "conference_id")
    val conferenceId: Int,

    @Deprecated("use dateZoned instead")
    val date: LocalDateTime,

    @ColumnInfo(name = "date_zoned", defaultValue = "0")
    val dateZoned: ZonedDateTime,
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "is_notified")
    var isNotified: Boolean = true
}
