package dev.forkhandles.values

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

class ValidationsTest {
    private val length_0 = ""
    private val length_1 = "1"
    private val length_9 = "123456789"
    private val length_10 = "1234567890"
    private val length_11 = "12345678901"

    @Test
    fun `string validations`() {
        assertThat(10.minLength(length_9), equalTo(false))
        assertThat(10.minLength(length_10), equalTo(true))
        assertThat(10.minLength(length_11), equalTo(true))

        assertThat(10.maxLength(length_9), equalTo(true))
        assertThat(10.maxLength(length_10), equalTo(true))
        assertThat(10.maxLength(length_11), equalTo(false))

        assertThat(10.exactLength(length_0), equalTo(false))
        assertThat(10.exactLength(length_9), equalTo(false))
        assertThat(10.exactLength(length_10), equalTo(true))
        assertThat(10.exactLength(length_11), equalTo(false))

        assertThat((1..10).length(length_0), equalTo(false))
        assertThat((1..10).length(length_1), equalTo(true))
        assertThat((1..10).length(length_10), equalTo(true))
        assertThat((1..10).length(length_11), equalTo(false))

        assertThat(".+".regex(length_0), equalTo(false))
        assertThat(".+".regex(length_1), equalTo(true))
    }

    @Test
    fun `number validations`() {
        assertThat(10.exactValue(9), equalTo(false))

        assertThat(10L.maxValue(10), equalTo(true))
        assertThat(10L.maxValue(11), equalTo(false))

        assertThat(10L.minValue(10), equalTo(true))
        assertThat(10L.minValue(9), equalTo(false))

        assertThat((1..10).between(0), equalTo(false))
        assertThat((1..10).between(11), equalTo(false))
    }

    @Test
    fun `combining validations`() {
        val notTen = !10.exactLength
        assertThat(notTen(length_9), equalTo(true))
        assertThat(notTen(length_10), equalTo(false))
        assertThat(notTen(length_11), equalTo(true))

        val oneToTen = 1.minLength.and(10.maxLength)
        assertThat(oneToTen(length_0), equalTo(false))
        assertThat(oneToTen(length_1), equalTo(true))
        assertThat(oneToTen(length_10), equalTo(true))
        assertThat(oneToTen(length_11), equalTo(false))

        val zeroOrEleven = 0.exactLength.or(11.exactLength)
        assertThat(zeroOrEleven(length_0), equalTo(true))
        assertThat(zeroOrEleven(length_1), equalTo(false))
        assertThat(zeroOrEleven(length_10), equalTo(false))
        assertThat(zeroOrEleven(length_11), equalTo(true))
    }
}
