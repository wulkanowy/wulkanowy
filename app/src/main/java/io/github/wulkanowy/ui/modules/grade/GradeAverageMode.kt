package io.github.wulkanowy.ui.modules.grade

import com.fredporciuncula.flow.preferences.Serializer as ISerializer

enum class GradeAverageMode(val value: String) {
    ALL_YEAR("all_year"),
    ONE_SEMESTER("only_one_semester"),
    BOTH_SEMESTERS("both_semesters");

    companion object {
        fun getByValue(value: String) = values().firstOrNull { it.value == value } ?: ONE_SEMESTER
    }

    object Serializer : ISerializer<GradeAverageMode> {
        override fun deserialize(serialized: String) = getByValue(serialized)

        override fun serialize(value: GradeAverageMode) = value.value
    }
}
