package dev.forkhandles.result4k

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class OnNullTests {

    @Test
    fun `does nothing on successful non-null result`() {
        fun subject(): Result<String, Nothing> = Success("non-null")
            .onNull { return Success("early-returned") }

        assertEquals(Success("non-null"), subject())
    }

    @Test
    fun `does nothing on unsuccessful result`() {
        fun subject(): Result<String, Exception> = resultFrom<String> { throw AnError("kaboom") }
            .onNull { return Success("early-returned") }

        assertEquals(Failure(AnError("kaboom")), subject())
    }

    @Test
    fun `early returns on successful null result`() {
        fun subject(): Result<String, Nothing> = Success(null)
            .onNull { return Success("early-returned") }
            .map { "mapped" }

        assertEquals(Success("early-returned"), subject())
    }

    @Test
    fun `continue the chain on successful non-null result`() {
        fun subject(): Result<String, Nothing> = Success("non-null")
            .onNull { return Success("early-returned") }
            .map { "mapped" }

        assertEquals(Success("mapped"), subject())
    }
}

private data class AnError(override val message: String) : Exception(message)
