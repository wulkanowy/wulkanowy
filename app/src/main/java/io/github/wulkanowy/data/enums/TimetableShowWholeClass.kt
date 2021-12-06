package io.github.wulkanowy.data.enums

enum class TimetableShowWholeClass(val value: String) {
    YES("yes"),
    NO("no"),
    SMALL("small");

    companion object {
        fun getByValue(value: String) = values().find { it.value == value } ?: NO
    }
}