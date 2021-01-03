package io.github.wulkanowy.data.db.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Preferences", indices = [Index(value = ["studentId", "key"], unique = true)])
data class Preference(

    val studentId: Int,

    val key: String,

    val value: String
): Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
