package com.example.calculist.domain

/**
 * Validation result for the add/edit item form.
 * A null field means "no error" for that field.
 */
data class ItemFormErrors(
    val name: String? = null,
    val value: String? = null,
    val quantity: String? = null
) {
    val hasErrors: Boolean get() = name != null || value != null || quantity != null
}

/**
 * Pure form validation. No Android dependencies - fully unit-testable.
 */
object ItemFormValidator {

    fun validate(name: String, value: String, quantity: String): ItemFormErrors {
        val nameError = if (name.isBlank()) "Name is required" else null

        val valueError = when {
            value.isBlank() -> "Value is required"
            value.toDoubleOrNull() == null -> "Enter a valid number"
            else -> null
        }

        val quantityError = when {
            quantity.isBlank() -> "Quantity is required"
            quantity.toDoubleOrNull() == null -> "Enter a valid number"
            (quantity.toDoubleOrNull() ?: 0.0) <= 0.0 -> "Must be greater than 0"
            else -> null
        }

        return ItemFormErrors(name = nameError, value = valueError, quantity = quantityError)
    }
}
