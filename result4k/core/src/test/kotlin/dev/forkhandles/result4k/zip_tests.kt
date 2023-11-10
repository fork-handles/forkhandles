package dev.forkhandles.result4k

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ZipTests {
    @Test
    fun `zip success`() {
        val result = zip(Success(123)) { it + 1 }
        assertEquals(Success(124), result)
    }

    @Test
    fun `zip double success`() {
        val r1: Result<String, SomeError> = Success("x")
        val r2: Result<Int, SomeError> = Success(123)

        val result: Result<Boolean, SomeError> = zip(r1, r2) { s: String, i: Int ->
            assertEquals("x", s)
            assertEquals(123, i)
            false
        }

        assertEquals(Success(false), result)
    }

    @Test
    fun `failure on first result of a double zip`() {
        val r1 = Failure(SomeError("r1"))
        val r2 = Success(123)

        val result = zip(r1, r2) { _, _ -> fail("shouldn't be called") }

        assertEquals(Failure(SomeError("r1")), result)
    }

    @Test
    fun `failure on second result of a double zip`() {
        val r1 = Success("x")
        val r2 = Failure(SomeError("r2"))

        val result = zip(r1, r2) { _, _ -> fail("shouldn't be called") }

        assertEquals(Failure(SomeError("r2")), result)
    }
}

class FlatZipTests {
    @Test
    fun `flatZip success`() {
        val result = flatZip(Success(123)) { Success(it + 1) }
        assertEquals(Success(124), result)
    }

    @Test
    fun `flatZip failure`() {
        val result = flatZip(Success(123)) { Failure(SomeError("flatZip failure")) }
        assertEquals(Failure(SomeError("flatZip failure")), result)
    }

    @Test
    fun `flatZip double success`() {
        val r1: Result<String, SomeError> = Success("x")
        val r2: Result<Int, SomeError> = Success(123)

        val result: Result<Boolean, SomeError> = flatZip(r1, r2) { s: String, i: Int ->
            assertEquals("x", s)
            assertEquals(123, i)
            Success(false)
        }

        assertEquals(Success(false), result)
    }

    @Test
    fun `failure on first result of a double flatZip`() {
        val r1 = Failure(SomeError("r1"))
        val r2 = Success(123)

        val result: Result<Boolean, SomeError> = flatZip(r1, r2) { _, _ ->
            fail("shouldn't be called")
        }

        assertEquals(Failure(SomeError("r1")), result)
    }

    @Test
    fun `failure on second result of a double flatZip`() {
        val r1 = Success("x")
        val r2 = Failure(SomeError("r2"))

        val result: Result<Boolean, SomeError> = flatZip(r1, r2) { _, _ ->
            fail("shouldn't be called")
        }

        assertEquals(Failure(SomeError("r2")), result)
    }

    @Test
    fun `failure on transformation of a double flatZip`() {
        val r1: Result<String, SomeError> = Success("x")
        val r2: Result<Int, SomeError> = Success(123)

        val result: Result<Boolean, SomeError> = flatZip(r1, r2) { s: String, i: Int ->
            assertEquals("x", s)
            assertEquals(123, i)
            Failure(SomeError("fail on transformation"))
        }

        assertEquals(Failure(SomeError("fail on transformation")), result)
    }
}

private data class SomeError(val message: String)
