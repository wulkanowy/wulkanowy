package io.github.wulkanowy.utils

import io.github.wulkanowy.data.db.entities.Semester
import org.threeten.bp.LocalDate.now

fun List<Semester>.getCurrentOrLast(): Semester {
    if (isEmpty()) throw RuntimeException("Empty semester list")

    return singleOrNull { semester -> now() in semester.start..semester.end }
        ?: singleOrNull { semester -> semester.semesterId == maxBy { it.semesterId }?.semesterId }
        ?: throw IllegalArgumentException("Current semester can be only one: ${joinToString(separator = "\n")}")
}
