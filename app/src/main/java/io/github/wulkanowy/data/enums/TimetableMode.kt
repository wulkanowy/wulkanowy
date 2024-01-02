package io.github.wulkanowy.data.enums

enum class TimetableMode(val value: String) {
    WHOLE_PLAN("yes"),
    ONLY_CURRENT_GROUP("no"),
    SMALL_OTHER_GROUP("small");

    companion object {
        fun getByValue(value: String) = entries.find { it.value == value } ?: ONLY_CURRENT_GROUP
    }
}
