package io.github.wulkanowy.ui.modules.grade

enum class GradeExpandMode(val value: String) {
    ONE("one"), UNLIMITED("any"), ALWAYS_EXPANDED("always");

    val isExpandable: Boolean
        get() = this != ALWAYS_EXPANDED

    companion object {
        fun getByValue(value: String) = values()
            .firstOrNull { it.value == value } ?: ONE
    }
}