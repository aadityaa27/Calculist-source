package com.example.calculist.domain

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class ItemFormValidatorTest {

    @Test
    fun `valid form has no errors`() {
        val errors = ItemFormValidator.validate(name = "Coffee", value = "250", quantity = "2")
        assertFalse(errors.hasErrors)
        assertNull(errors.name)
        assertNull(errors.value)
        assertNull(errors.quantity)
    }

    @Test
    fun `blank name is rejected`() {
        val errors = ItemFormValidator.validate(name = "   ", value = "250", quantity = "2")
        assertTrue(errors.hasErrors)
        assertNotNull(errors.name)
    }

    @Test
    fun `blank value is rejected`() {
        val errors = ItemFormValidator.validate(name = "Coffee", value = "", quantity = "2")
        assertTrue(errors.hasErrors)
        assertNotNull(errors.value)
    }

    @Test
    fun `non numeric value is rejected`() {
        val errors = ItemFormValidator.validate(name = "Coffee", value = "abc", quantity = "2")
        assertTrue(errors.hasErrors)
        assertNotNull(errors.value)
    }

    @Test
    fun `blank quantity is rejected`() {
        val errors = ItemFormValidator.validate(name = "Coffee", value = "250", quantity = "")
        assertTrue(errors.hasErrors)
        assertNotNull(errors.quantity)
    }

    @Test
    fun `zero quantity is rejected`() {
        val errors = ItemFormValidator.validate(name = "Coffee", value = "250", quantity = "0")
        assertTrue(errors.hasErrors)
        assertNotNull(errors.quantity)
    }

    @Test
    fun `negative quantity is rejected`() {
        val errors = ItemFormValidator.validate(name = "Coffee", value = "250", quantity = "-3")
        assertTrue(errors.hasErrors)
        assertNotNull(errors.quantity)
    }

    @Test
    fun `fractional quantity is accepted`() {
        val errors = ItemFormValidator.validate(name = "Flour", value = "200", quantity = "0.5")
        assertFalse(errors.hasErrors)
    }

    @Test
    fun `negative value is accepted`() {
        val errors = ItemFormValidator.validate(name = "Discount", value = "-25", quantity = "1")
        assertFalse(errors.hasErrors)
    }

    @Test
    fun `multiple errors are reported together`() {
        val errors = ItemFormValidator.validate(name = "", value = "x", quantity = "0")
        assertTrue(errors.hasErrors)
        assertNotNull(errors.name)
        assertNotNull(errors.value)
        assertNotNull(errors.quantity)
        assertEquals(3, listOf(errors.name, errors.value, errors.quantity).count { it != null })
    }
}
