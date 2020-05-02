package io.github.wulkanowy.ui.modules.grade

import io.github.wulkanowy.data.db.entities.Grade

data class GradeDetailsWithAverage(
    val subject: String,
    val average: Double,
    val points: String,
    val grades: List<Grade>
)
