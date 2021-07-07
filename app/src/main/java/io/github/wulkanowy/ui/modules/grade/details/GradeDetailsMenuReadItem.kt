package io.github.wulkanowy.ui.modules.grade.details

import java.time.LocalDate

data class GradeDetailsMenuReadItem(
    val days: Int?,
    val id: Int
) {
    val date: LocalDate?
        get() = days?.let { LocalDate.now().minusDays(it.toLong()) }
}
