package io.github.wulkanowy.ui.modules.grade

import io.github.wulkanowy.R
import io.github.wulkanowy.data.db.entities.Grade
import io.github.wulkanowy.ui.modules.grade.GradeColorTheme.*
import io.github.wulkanowy.ui.modules.grade.GradeColorTheme.Companion.DEFAULT
import io.github.wulkanowy.utils.getGradeColor
import java.io.Serializable

enum class GradeColorTheme(val value: String) : Serializable {
    VULCAN("vulcan"), MATERIAL("material"), GRADE_COLOR("grade_color");

    companion object {
        val DEFAULT = VULCAN

        fun getByValue(value: String) = values().firstOrNull { it.value == value } ?: DEFAULT
    }
}

fun GradeColorTheme?.orDefault() = when(this) {
    null -> DEFAULT
    else -> this
}

fun Grade.getBackgroundColor(theme: GradeColorTheme) = when (theme) {
    GRADE_COLOR -> getGradeColor()
    MATERIAL -> when (value.toInt()) {
        6 -> R.color.grade_material_six
        5 -> R.color.grade_material_five
        4 -> R.color.grade_material_four
        3 -> R.color.grade_material_three
        2 -> R.color.grade_material_two
        1 -> R.color.grade_material_one
        else -> R.color.grade_material_default
    }
    VULCAN -> when (value.toInt()) {
        6 -> R.color.grade_vulcan_six
        5 -> R.color.grade_vulcan_five
        4 -> R.color.grade_vulcan_four
        3 -> R.color.grade_vulcan_three
        2 -> R.color.grade_vulcan_two
        1 -> R.color.grade_vulcan_one
        else -> R.color.grade_vulcan_default
    }
}