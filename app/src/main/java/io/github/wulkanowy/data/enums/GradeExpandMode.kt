package io.github.wulkanowy.data.enums

enum class GradeExpandMode(val value: String) {
    ONE("one"),
    UNLIMITED("any"),
    ALWAYS_EXPANDED("always");

    companion object {
        fun getByValue(value: String) = entries.find { it.value == value } ?: ONE
    }
}
