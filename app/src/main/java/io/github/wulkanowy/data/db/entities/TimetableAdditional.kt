package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.util.UUID

@Entity(tableName = "TimetableAdditional")
data class TimetableAdditional(

    @ColumnInfo(name = "student_id")
    val studentId: Int,

    @ColumnInfo(name = "diary_id")
    val diaryId: Int,

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
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "repeat_id", defaultValue = "NULL")
    var repeatId: UUID? = null

    @ColumnInfo(name = "is_added_by_user", defaultValue = "0")
    var isAddedByUser: Boolean = false
}
