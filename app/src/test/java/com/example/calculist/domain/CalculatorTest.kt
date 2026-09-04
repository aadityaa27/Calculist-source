package com.example.calculist.domain

import com.example.calculist.data.ListItem
import org.junit.Assert.assertEquals
import org.junit.Test

class CalculatorTest {

    private fun item(
        name: String,
        value: Double,
        quantity: Double,
        completed: Boolean = false
    ) = ListItem(
        listId = 1L,
        name = name,
        value = value,
        quantity = quantity,
        completed = completed
    )

    @Test
    fun `contribution is value times quantity`() {
        val item = item("A", value = 100.0, quantity = 3.0)
        assertEquals(300.0, Calculator.contribution(item), 0.0001)
    }

    @Test
    fun `total sums all contributions`() {
        val items = listOf(
            item("A", 10_000.0, 1.0),
            item("B", 5_000.0, 1.0),
            item("C", 2_500.0, 1.0)
        )
        val stats = Calculator.computeStats(items)
        assertEquals(17_500.0, stats.total, 0.0001)
        assertEquals(3, stats.itemCount)
    }

    @Test
    fun `example matches spec average`() {
        val items = listOf(
            item("A", 10_000.0, 1.0),
            item("B", 5_000.0, 1.0),
            item("C", 2_500.0, 1.0)
        )
        val stats = Calculator.computeStats(items)
        assertEquals(17_500.0 / 3.0, stats.average, 0.0001)
    }

    @Test
    fun `completed items are excluded from total average and quantity`() {
        val items = listOf(
            item("A", 100.0, 2.0),
            item("B", 50.0, 4.0, completed = true)
        )
        val stats = Calculator.computeStats(items)
        assertEquals(200.0, stats.total, 0.0001)
        assertEquals(200.0, stats.average, 0.0001)
        assertEquals(2.0, stats.totalQuantity, 0.0001)
        assertEquals(1, stats.itemCount)
    }

    @Test
    fun `empty list yields zeroed stats`() {
        val stats = Calculator.computeStats(emptyList())
        assertEquals(0.0, stats.total, 0.0001)
        assertEquals(0.0, stats.average, 0.0001)
        assertEquals(0.0, stats.totalQuantity, 0.0001)
        assertEquals(0, stats.itemCount)
    }

    @Test
    fun `all completed list yields zeroed stats`() {
        val items = listOf(
            item("A", 100.0, 2.0, completed = true),
            item("B", 50.0, 4.0, completed = true)
        )
        val stats = Calculator.computeStats(items)
        assertEquals(0.0, stats.total, 0.0001)
        assertEquals(0.0, stats.average, 0.0001)
        assertEquals(0.0, stats.totalQuantity, 0.0001)
        assertEquals(0, stats.itemCount)
    }

    @Test
    fun `negative values are supported`() {
        val items = listOf(
            item("Discount", -25.0, 1.0),
            item("Fee", 100.0, 1.0)
        )
        val stats = Calculator.computeStats(items)
        assertEquals(75.0, stats.total, 0.0001)
        assertEquals(37.5, stats.average, 0.0001)
    }

    @Test
    fun `fractional quantities are supported`() {
        val items = listOf(
            item("Flour kg", 200.0, 0.5),
            item("Milk L", 60.0, 1.5)
        )
        val stats = Calculator.computeStats(items)
        assertEquals(190.0, stats.total, 0.0001)
        assertEquals(2.0, stats.totalQuantity, 0.0001)
        assertEquals(95.0, stats.average, 0.0001)
    }

    @Test
    fun `total quantity counts only active items`() {
        val items = listOf(
            item("A", 10.0, 3.0),
            item("B", 20.0, 2.0, completed = true)
        )
        val stats = Calculator.computeStats(items)
        assertEquals(3.0, stats.totalQuantity, 0.0001)
    }

    @Test
    fun `decimal precision within tolerance`() {
        val items = listOf(
            item("A", 0.1, 1.0),
            item("B", 0.2, 1.0)
        )
        val stats = Calculator.computeStats(items)
        assertEquals(0.3, stats.total, 0.0001)
        assertEquals(0.15, stats.average, 0.0001)
    }

    @Test
    fun `completedCount and grandTotal correctly computed`() {
        val items = listOf(
            item("Active Item", 50.0, 2.0, completed = false),
            item("Done Item", 25.0, 4.0, completed = true)
        )
        val stats = Calculator.computeStats(items)
        assertEquals(100.0, stats.total, 0.0001)
        assertEquals(100.0, stats.completedTotal, 0.0001)
        assertEquals(200.0, stats.grandTotal, 0.0001)
        assertEquals(1, stats.itemCount)
        assertEquals(1, stats.completedCount)
    }

    @Test
    fun `formatListAsText formats clean shareable text`() {
        val items = listOf(
            item("Apples", 2.50, 4.0, completed = false),
            item("Milk", 3.00, 1.0, completed = true)
        )
        val stats = Calculator.computeStats(items)
        val text = Calculator.formatListAsText("Groceries", items, stats)
        org.junit.Assert.assertTrue(text.contains("Groceries"))
        org.junit.Assert.assertTrue(text.contains("[ ] Apples"))
        org.junit.Assert.assertTrue(text.contains("[✓] Milk"))
        org.junit.Assert.assertTrue(text.contains("Active Total: $10"))
    }
}
