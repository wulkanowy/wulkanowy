package io.github.wulkanowy.utils

fun Double.round(round: Int): Double {
    return String.format("%.${round}f", this).replace(",", ".").toDouble()
}
