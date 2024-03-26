package io.github.wulkanowy.utils

import java.util.EnumMap
import java.util.EnumSet

infix fun <T> List<T>.uniqueSubtract(other: List<T>): List<T> {
    val list = toMutableList()
    other.forEach {
        list.remove(it)
    }
    return list.toList()
}

inline fun <reified T : Enum<T>> Iterable<T>.toEnumSet(): EnumSet<T> =
    EnumSet.noneOf(T::class.java).also {
        for (item in this) {
            it.add(item)
        }
    }


inline fun <reified K : Enum<K>, V> EnumMap() = EnumMap<K, V>(K::class.java)
