package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.time.LocalDateTime
import java.time.ZonedDateTime

@Entity(tableName = "MobileDevices")
data class MobileDevice(

    @ColumnInfo(name = "student_id")
    val userLoginId: Int,

    @ColumnInfo(name = "device_id")
    val deviceId: Int,

    val name: String,

    @Deprecated("use dateZoned instead")
    val date: LocalDateTime,

    @ColumnInfo(name = "date_zoned", defaultValue = "0")
    val dateZoned: ZonedDateTime,
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
