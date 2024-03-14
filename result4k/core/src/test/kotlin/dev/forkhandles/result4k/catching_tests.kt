package dev.forkhandles.result4k

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals
import kotlin.test.fail

class CatchingTests {
    @Test
    fun `check catch specific error`() {
        val result = resultFromCatching<IllegalArgumentException, _> {
            throw IllegalArgumentException("error")
        }
        val failure = result as? Failure
        assertEquals("error", failure?.reason?.message)
    }

    @Test
    fun `check do not specific error`() {
        val exception = assertThrows<IllegalArgumentException> {
            resultFromCatching<IllegalStateException, _> {
                throw IllegalArgumentException("error")
            }
            fail("should not reach here")
        }
        assertEquals("error", exception.message)
    }

    @Test
    fun `check success`() {
        val result = resultFromCatching<IllegalArgumentException, _> {
            "success"
        }
        val success = result as? Success
        assertEquals("success", success?.value)
    }
}
