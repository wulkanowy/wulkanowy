package io.github.wulkanowy.data.db.entities

import androidx.room.Embedded
import androidx.room.Relation

data class StudentInfoWithGuardians(

    @Embedded
    val studentInfo: StudentInfo,

    @Relation(parentColumn = "student_id", entityColumn = "student_id")
    val studentGuardians: List<StudentGuardian>
)