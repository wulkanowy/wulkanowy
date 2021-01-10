package io.github.wulkanowy.utils

import io.github.wulkanowy.data.db.entities.Semester
import java.time.LocalDate

fun getRefreshKey(name: String, semester: Semester, start: LocalDate, end: LocalDate): String {
    return "${name}_${semester.studentId}_${semester.semesterId}_${start.monday}_${end.sunday}"
}

fun getRefreshKey(name: String, semester: Semester): String {
    return "${name}_${semester.studentId}_${semester.semesterId}"
}
