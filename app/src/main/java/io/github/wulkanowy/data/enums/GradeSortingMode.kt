package io.github.wulkanowy.data.enums

import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.ui.modules.grade.GradeSubject

enum class GradeSortingMode(val value: String) {
    ALPHABETIC("alphabetic"),
    DATE("date"),
    AVERAGE("average");

    fun sort(iterable: Iterable<GradeSubject>) = when (this) {
        DATE -> iterable.sortedByDescending { it.grades.maxByOrNull(Grade::date)?.date }
        ALPHABETIC -> iterable.sortedBy { it.subject.lowercase() }
        AVERAGE -> iterable.sortedByDescending(GradeSubject::average)
    }

    companion object {
        fun getByValue(value: String) = values().find { it.value == value } ?: ALPHABETIC
    }
}
