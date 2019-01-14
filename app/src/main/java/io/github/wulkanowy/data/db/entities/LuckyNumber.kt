package io.github.wulkanowy.data.db.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDate
import java.io.Serializable

@Entity(tableName = "LuckyNumbers")
data class LuckyNumber (

    @ColumnInfo(name = "student_id")
    var studentId: Int,

    var date: LocalDate,

    var luckyNumber: Int

) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

}
