package com.example.calculist.ui

import java.util.Locale

/** Number formatting helpers used across the UI. */
object Formatters {

    /** Grouped, up to 2 decimals, trailing zeros trimmed: 17500 -> "17,500", 5833.333 -> "5,833.33". */
    fun money(value: Double): String =
        String.format(Locale.US, "%,.2f", value)
            .trimEnd('0')
            .trimEnd('.')

    /** Compact form without grouping: 10.0 -> "10", 0.5 -> "0.5", -2.5 -> "-2.5". */
    fun compact(value: Double): String {
        val rounded = Math.rint(value)
        return if (value == rounded) {
            value.toLong().toString()
        } else {
            String.format(Locale.US, "%.2f", value)
                .trimEnd('0')
                .trimEnd('.')
        }
    }
}
