package dev.forkhandles.values

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.present
import org.junit.jupiter.api.Test

class ValidationsTest {

    @Test
    fun `deprecated helpers delegate to validators`() {
        assertThat(10.minLength("9"), present())
        assertThat(10.minLength("1234567890"), absent())

        assertThat(10.maxLength("1234567890"), absent())
        assertThat(10.maxLength("12345678901"), present())

        assertThat(10.exactLength("1234567890"), absent())
        assertThat(10.exactLength("123456789"), present())

        assertThat((1..10).length(""), present())
        assertThat((1..10).length("1"), absent())

        assertThat(".+".regex(""), present())
        assertThat(".+".regex("a"), absent())

        assertThat(10L.maxValue(10), absent())
        assertThat(10L.maxValue(11), present())

        assertThat(10L.minValue(10), absent())
        assertThat(10L.minValue(9), present())

        assertThat((1..10).between(5), absent())
        assertThat((1..10).between(0), present())
        assertThat((1..10).between(11), present())

        assertThat(10.exactValue(10), absent())
        assertThat(10.exactValue(9), present())
    }
}
