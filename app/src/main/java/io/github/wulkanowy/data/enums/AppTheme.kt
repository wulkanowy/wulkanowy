package io.github.wulkanowy.data.enums

enum class AppTheme(val value: String) {
    SYSTEM("system"),
    LIGHT("light"),
    DARK("dark"),
    BLACK("black");

    companion object {
        fun getByValue(value: String) = entries.find { it.value == value } ?: LIGHT
    }
}
