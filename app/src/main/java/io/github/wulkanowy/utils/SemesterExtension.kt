package io.github.wulkanowy.utils

import io.github.wulkanowy.data.db.entities.Semester
import org.threeten.bp.LocalDate.now

inline val Semester.isCurrent: Boolean
    get() = now() in start..end

fun List<Semester>.getCurrentOrLast(): Semester {
    if (isEmpty()) throw RuntimeException("Empty semester list")

    return singleOrNull { it.isCurrent }
        ?: sortedByDescending { it.semesterId }.singleOrNull()
        ?: throw IllegalArgumentException("Current semester can be only one: ${joinToString(separator = "\n")}")
}
