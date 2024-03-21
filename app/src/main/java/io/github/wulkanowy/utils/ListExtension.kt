package io.github.wulkanowy.utils

infix fun <T> List<T>.uniqueSubtract(other: List<T>): List<T> {
    val list = toMutableList()
    other.forEach {
        list.remove(it)
    }
    return list.toList()
}

fun <T> Iterable<T>.filterIf(condition: Boolean, predicate: (T) -> Boolean) = if (condition) {
    filter(predicate)
} else {
    this
}
