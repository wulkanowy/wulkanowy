package io.github.wulkanowy.ui.modules.calculator

import io.github.wulkanowy.data.repositories.PreferencesRepository
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class Calculator @Inject constructor(val preferencesRepository: PreferencesRepository) {
    val defaultList = CalculatorItemList().apply {
        this.addAll(preferencesRepository.calculatorItems)
    }

    var onListUpdated: () -> Unit = {}

    fun listUpdated() {
        try {
            onListUpdated()
        } catch (_: Exception) {

        }
    }

    private fun saveData() {
        preferencesRepository.calculatorItems = defaultList
    }

    private val basicGradeRegex = "^(\\d+)([+-]?)$".toRegex()

    fun addItem(calculatorItem: CalculatorItem) {
        defaultList.add(calculatorItem)
        saveData()
        listUpdated()
    }

    fun clear() {
        defaultList.clear()
        saveData()
        listUpdated()
    }

    fun addItems(items: List<CalculatorItem>) {
        defaultList.addAll(items)
        saveData()
        listUpdated()
    }

    fun deleteItem(calculatorItem: CalculatorItem) {
        defaultList.remove(calculatorItem)
        saveData()
        listUpdated()
    }

    fun getMinusValue(): Double = preferencesRepository.calculatorMinusValue
        ?: preferencesRepository.gradeMinusModifier

    fun getPlusValue(): Double =
        preferencesRepository.calculatorPlusValue ?: preferencesRepository.gradePlusModifier

    fun parseGrade(grade: String): Double? = toGradeOrNull(
        grade,
        getMinusValue(),
        getPlusValue()
    )

    fun parseWeight(weight: String): Double? = weight.toDoubleOrNull()

    private fun isBasicGradeString(t: String): Boolean = basicGradeRegex.matchesAt(t, 0)

    private fun toDoubleOrNull(t: String) = t.toDoubleOrNull()

    private fun asBasicGradeString(t: String, plus: Double, minus: Double): Double? {
        val match = basicGradeRegex.matchAt(t, 0) ?: return null
        val grade = match.groups[1]?.value?.toDouble() ?: return null
        return when (match.groups[2]?.value) {
            "+" -> grade + plus
            "-" -> grade - minus
            "" -> grade
            else -> null
        }
    }

    private fun toGradeOrNull(s: String, plusValue: Double, minusValue: Double) =
        if (isBasicGradeString(s)) {
            asBasicGradeString(s, plusValue, minusValue)
        } else {
            toDoubleOrNull(s)
        }

    fun makeItem(title: String?, grade: Double, weight: Double, originalGrade: String?) =
        CalculatorItem(grade, weight, title, null, originalGrade)
}
