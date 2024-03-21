package io.github.wulkanowy.ui.modules.grade.details

import io.github.wulkanowy.data.db.entities.Grade as GradeEntity

enum class ViewType(val id: Int) {
    HEADER(1),
    ITEM(2)
}

sealed class GradeDetailsItem(val viewType: ViewType) {
    data class Header(
        val subject: String,
        val average: Double?,
        val pointsSum: String?,
        val grades: List<GradeDetailsItem.Grade>
    ) : GradeDetailsItem(ViewType.HEADER) {
        val newGrades: Int
            get() = grades.count { !it.grade.isRead }
    }

    data class Grade(val grade: GradeEntity) :
        GradeDetailsItem(ViewType.ITEM)
}
