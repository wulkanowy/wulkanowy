package io.github.wulkanowy.utils

import io.github.wulkanowy.ui.modules.calculator.CalculatorItem

fun Iterable<CalculatorItem>.calculateAverage(): Double {
    return this.sumOf { it.weight * it.grade } / this.sumOf { it.weight }
}
