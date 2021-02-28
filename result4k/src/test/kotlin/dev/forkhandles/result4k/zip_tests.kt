package dev.forkhandles.result4k

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.fail

class ZipTests {
    private data class ExampleException(val msg: String) : Exception(msg)

    @Test
    fun `success zip returns value`() {
        val r: Result<String, ExampleException> = Success("x")
        val z: Result<Int, ExampleException> = zip(r) { 2 }

        assertEquals(Success(2), z)
    }

    @Test
    fun `success flatzip returns value`() {
        val r: Result<String, ExampleException> = Success("x")
        val z: Result<Int, ExampleException> = flatZip(r) { Success(2) }

        assertEquals(Success(2), z)
    }

    @Test
    fun `failure flatzip returns value`() {
        val r: Result<String, ExampleException> = Success("x")
        val z: Result<Int, ExampleException> = flatZip(r) { Failure(ExampleException("flatzip failure")) }

        assertEquals(Failure(ExampleException("flatzip failure")), z)
    }

    @Test
    fun `success double flatzip returns value`() {
        val r1: Result<String, ExampleException> = Success("x")
        val r2: Result<Int, ExampleException> = Success(3)

        val z: Result<Boolean, ExampleException> = flatZip(r1, r2) { a: String, b: Int ->
            assertEquals("x", a)
            assertEquals(3, b)
            Success(false)
        }

        assertEquals(Success(false), z)
    }

    @Test
    fun `failure on first result of a double flatzip returns value`() {
        val r1: Result<String, ExampleException> = Failure(ExampleException("fail r1"))
        val r2: Result<Int, ExampleException> = Success(3)

        val z: Result<Boolean, ExampleException> = flatZip(r1, r2) { _: String, _: Int ->
            fail("shouldn't be called")
        }

        assertEquals(Failure(ExampleException("fail r1")), z)
    }

    @Test
    fun `failure on second result of a double flatzip returns value`() {
        val r1: Result<String, ExampleException> = Success("x")
        val r2: Result<Int, ExampleException> = Failure(ExampleException("fail r2"))

        val z: Result<Boolean, ExampleException> = flatZip(r1, r2) { _: String, _: Int ->
            fail("shouldn't be called")
        }

        assertEquals(Failure(ExampleException("fail r2")), z)
    }


    @Test
    fun `failure on transfromation of a double flatzip returns value`() {
        val r1: Result<String, ExampleException> = Success("x")
        val r2: Result<Int, ExampleException> = Success(3)

        val z: Result<Boolean, ExampleException> = flatZip(r1, r2) { a: String, b: Int ->
            assertEquals("x", a)
            assertEquals(3, b)
            Failure(ExampleException("fail on transformation"))
        }

        assertEquals(Failure(ExampleException("fail on transformation")), z)
    }
}
