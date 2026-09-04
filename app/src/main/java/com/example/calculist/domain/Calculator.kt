package com.example.calculist.domain

import com.example.calculist.data.ListItem

/**
 * Aggregated statistics for a list, computed from its items.
 * Completed items are excluded from every figure.
 */
data class ListStats(
    val total: Double = 0.0,
    val average: Double = 0.0,
    val totalQuantity: Double = 0.0,
    val itemCount: Int = 0
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
     */
    fun computeStats(items: List<ListItem>): ListStats {
        val active = items.filter { !it.completed }
        val total = active.sumOf { it.value * it.quantity }
        val count = active.size
        return ListStats(
            total = total,
            average = if (count == 0) 0.0 else total / count,
            totalQuantity = active.sumOf { it.quantity },
            itemCount = count
        )
    }
}
