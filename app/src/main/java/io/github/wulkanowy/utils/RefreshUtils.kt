package io.github.wulkanowy.utils

import io.github.wulkanowy.data.db.entities.Semester
import io.github.wulkanowy.data.db.entities.Student
import io.github.wulkanowy.data.enums.MessageFolder
import java.time.LocalDate

fun getRefreshKey(name: String, semester: Semester, start: LocalDate, end: LocalDate): String {
    return "${name}_${semester.studentId}_${semester.semesterId}_${start.monday}_${end.sunday}"
}

fun getRefreshKey(name: String, semester: Semester): String {
    return "${name}_${semester.studentId}_${semester.semesterId}"
}

fun getRefreshKey(name: String, student: Student): String {
    return "${name}_${student.userLoginId}"
}

fun getRefreshKey(name: String, student: Student, folder: MessageFolder): String {
    return "${name}_${student.id}_${folder.id}"
}
