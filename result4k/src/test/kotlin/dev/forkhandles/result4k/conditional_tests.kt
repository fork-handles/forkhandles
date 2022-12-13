package dev.forkhandles.result4k

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RecoverIfTests {

    @Test
    fun `never re-recover a success`() {
        val r = Success("a").recoverIf({ true }, { "b" })

        assertEquals(Success("a"), r)
    }

    @Test
    fun `recover a failure when condition is met`() {
        val r = Failure(1).recoverIf({ it == 1 }, { "a" })

        assertEquals(Success("a"), r)
    }

    @Test
    fun `don't recover a failure when condition is not met`() {
        val r = Failure(1).recoverIf({ it == 2 }, { "a" })

        assertEquals(Failure(1), r)
    }
}

class FailIfTests {

    @Test
    fun `never re-fail a failure`() {
        val r = Failure(1).failIf({ true }, { 2 })

        assertEquals(Failure(1), r)
    }

    @Test
    fun `fail a success when condition is met`() {
        val r = Success("a").failIf({ it == "a" }, { 1 })

        assertEquals(Failure(1), r)
    }

    @Test
    fun `don't fail a success when condition is not met`() {
        val r = Success("a").failIf({ it == "b" }, { 1 })

        assertEquals(Success("a"), r)
    }
}
