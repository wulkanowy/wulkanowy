package io.github.wulkanowy.ui.modules.calculator

import io.github.wulkanowy.data.repositories.PreferencesRepository
import io.mockk.*
import io.mockk.impl.annotations.MockK
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CalculatorTest {
    @MockK
    lateinit var prefRepo: PreferencesRepository

    private lateinit var calculator: Calculator

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        every {prefRepo getProperty "calculatorItems"} returns listOf<CalculatorItem>()
        every {prefRepo setProperty  "calculatorItems" value any<List<CalculatorItem>>()} just Runs
        calculator = Calculator(prefRepo)
    }

    @Test
    fun `addItem calls 'onListUpdated'`() {
        var calls = false
        calculator.onListUpdated = {calls = true}
        assertFalse { calls }
        calculator.addItem(CalculatorItem(5.0, 5.0, null, null, null))
        assertTrue { calls }
    }

    @Test
    fun `deleteItem calls 'onListUpdated'`() {
        var calls = false
        calculator.onListUpdated = {calls = true}
        assertFalse { calls }
        val element = CalculatorItem(5.0, 5.0, null, null, null)
        calculator.defaultList.add(element)
        calculator.deleteItem(element)
        assertTrue { calls }
    }

    @Test
    fun `addItem adds item to list`() {
        val e = CalculatorItem(5.0, 5.0, null, null, null)
        assertFalse { calculator.defaultList.contains(e) }
        calculator.addItem(e)
        assertTrue { calculator.defaultList.contains(e) }
    }

    @Test
    fun `deleteItem takes item from list`() {
        val e = CalculatorItem(5.0, 5.0, null, null, null)
        calculator.defaultList.add(e)
        assertTrue { calculator.defaultList.contains(e) }
        calculator.deleteItem(e)
        assertFalse { calculator.defaultList.contains(e) }
    }

    @Test
    fun `getMinusValue returns prefRepo-calculatorMinusValue if not null`() {
        val e = 4.33
        every { prefRepo.calculatorMinusValue } returns e
        assertEquals(e, calculator.getMinusValue())
    }

    @Test
    fun `getPlusValue returns prefRepo-calculatorPlusValue if not null`() {
        val e = 3.52
        every { prefRepo.calculatorPlusValue } returns e
        assertEquals(e, calculator.getPlusValue())
    }

    @Test
    fun `getMinusValue returns prefRepo-gradeMinusModifier if prefRepo-calculatorMinusValue is null`() {
        val e = 1.23
        every { prefRepo.calculatorMinusValue } returns null
        every {prefRepo.gradeMinusModifier} returns e
        assertEquals(e, calculator.getMinusValue())
    }

    @Test
    fun `getPlusValue returns prefRepo-gradePlusModifier if prefRepo-calculatorPlusValue is null`() {
        val e = -5.4
        every { prefRepo.calculatorPlusValue } returns null
        every {prefRepo.gradePlusModifier} returns e
        assertEquals(e, calculator.getPlusValue())
    }
}
