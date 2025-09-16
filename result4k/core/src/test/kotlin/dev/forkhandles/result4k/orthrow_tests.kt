package dev.forkhandles.result4k

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

class OrThrowTests {
    private class ExampleException(message: String? = null) : Exception(message)

    @Test
    fun `success returns value`() {
        val r: Result<String, ExampleException> = Success("x")

        assertEquals("x", r.orThrow())
    }

    @Test
    fun `failure is thrown if an exception`() {
        val r: Result<String, ExampleException> = Failure(ExampleException())

        assertThrows<ExampleException> { r.orThrow() }
    }

    @Test
    fun `convenience function to convert failure to exception and throw`() {
        val r: Result<Int, String> = Failure("error value")
        val e = assertThrows<ExampleException> {
            r.orThrow { ExampleException(it) }
        }

        assertEquals("error value", e.message)
    }
}
