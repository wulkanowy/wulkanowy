package io.github.wulkanowy.ui.modules.grade.details

import io.github.wulkanowy.data.db.entities.Grade

data class GradeDetailsHeader(
    val subject: String,
    val number: Int,
    val average: Double?,
    val pointsSum: String?,
    var newGrades: Int,
    val grades: List<GradeDetailsItem<Grade>>
)
