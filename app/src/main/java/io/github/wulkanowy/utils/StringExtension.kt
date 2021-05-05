package io.github.wulkanowy.utils

import java.util.Locale

inline fun String?.ifNullOrBlank(defaultValue: () -> String) =
    if (isNullOrBlank()) defaultValue() else this

fun String.capitalise() = replaceFirstChar {
    if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
}

fun String.decapitalise() = replaceFirstChar {
    it.lowercase(Locale.getDefault())
}
