package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "AttendanceSubjects")
data class AttendanceSubject(
    @ColumnInfo(name = "diary_id")
    private val diaryId: Long? = null,

    @ColumnInfo(name = "real_id")
    private val realId: Int = 0,

    @ColumnInfo(name = "name")
    private val name: String? = null
) {

    @PrimaryKey(autoGenerate = true)
    private val id: Long? = null
}
