package io.github.wulkanowy.data.db.entities

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "Grades")
data class Grade(

        @ColumnInfo(name = "semester_id")
        var semesterId: String,

        @ColumnInfo(name = "student_id")
        var studentId: String,

        var subject: String,

        var entry: String,

        var value: Int,

        var modifier: Float,

        var comment: String,

        var color: String,

        @ColumnInfo(name = "grade_symbol")
        var gradeSymbol: String,

        var description: String,

        var weight: String,

        var weightValue: Int,

        var date: String,

        var teacher: String
) : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0

    @ColumnInfo(name = "is_new")
    var isNew: Boolean = false
}
