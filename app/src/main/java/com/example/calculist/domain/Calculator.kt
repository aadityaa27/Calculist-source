package com.example.calculist.domain

import com.example.calculist.data.ListItem

/**
 * Aggregated statistics for a list, computed from its items.
 * Active figures exclude completed items, while completion metrics provide progress context.
 */
data class ListStats(
    val total: Double = 0.0,
    val average: Double = 0.0,
    val totalQuantity: Double = 0.0,
    val itemCount: Int = 0,
    val completedCount: Int = 0,
    val completedTotal: Double = 0.0,
    val grandTotal: Double = 0.0
)

/**
 * Pure calculation logic. No Android dependencies - fully unit-testable.
 */
object Calculator {

    /** Contribution of a single item: Value x Quantity. */
    fun contribution(item: ListItem): Double = item.value * item.quantity

    /**
     * Computes the list statistics.
     * - Total = sum of Value x Quantity over non-completed items
     * - Average = Total / number of non-completed items (0 when empty)
     * - Total Quantity = sum of quantities over non-completed items
     * - Completed metrics track checked items for progress
     */
    fun computeStats(items: List<ListItem>): ListStats {
        val active = items.filter { !it.completed }
        val completed = items.filter { it.completed }
        val total = active.sumOf { it.value * it.quantity }
        val completedTotal = completed.sumOf { it.value * it.quantity }
        val count = active.size
        return ListStats(
            total = total,
            average = if (count == 0) 0.0 else total / count,
            totalQuantity = active.sumOf { it.quantity },
            itemCount = count,
            completedCount = completed.size,
            completedTotal = completedTotal,
            grandTotal = total + completedTotal
        )
    }

    /**
     * Formats the list into a clean shareable plain text receipt / breakdown.
     */
    fun formatListAsText(listName: String, items: List<ListItem>, stats: ListStats): String {
        val sb = StringBuilder()
        sb.appendLine("📋 $listName — CalcuList Summary")
        sb.appendLine("--------------------------------")
        if (items.isEmpty()) {
            sb.appendLine("No items in list.")
        } else {
            items.forEach { item ->
                val check = if (item.completed) "[✓]" else "[ ]"
                val valueStr = if (item.quantity == 1.0) {
                    "$${com.example.calculist.ui.Formatters.money(item.value)}"
                } else {
                    "$${com.example.calculist.ui.Formatters.compact(item.value)} × ${com.example.calculist.ui.Formatters.compact(item.quantity)} = $${com.example.calculist.ui.Formatters.money(contribution(item))}"
                }
                sb.appendLine("$check ${item.name}: $valueStr")
                if (item.notes.isNotBlank()) {
                    sb.appendLine("    Note: ${item.notes}")
                }
            }
        }
        sb.appendLine("--------------------------------")
        sb.appendLine("Active Total: $${com.example.calculist.ui.Formatters.money(stats.total)} (${stats.itemCount} items)")
        if (stats.completedCount > 0) {
            sb.appendLine("Completed: $${com.example.calculist.ui.Formatters.money(stats.completedTotal)} (${stats.completedCount} items)")
            sb.appendLine("Grand Total: $${com.example.calculist.ui.Formatters.money(stats.grandTotal)}")
        }
        sb.appendLine("Average per active item: $${com.example.calculist.ui.Formatters.money(stats.average)}")
        sb.appendLine("Total units: ${com.example.calculist.ui.Formatters.compact(stats.totalQuantity)}")
        return sb.toString()
    }
}
