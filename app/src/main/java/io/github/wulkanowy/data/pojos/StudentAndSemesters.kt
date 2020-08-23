package io.github.wulkanowy.data.pojos

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student

data class StudentAndSemesters(
    val student: Student,
    val semesters: List<Semester>
)
