package dev.forkhandles.tx

import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertSame

class TransactorCleanupTest {
    private class Retryable : Exception("retry me")

    private class FlakyCleanup(
        private val failRollback: Boolean = false,
        private val failDestroy: Boolean = false,
    ) : Transactor<Unit, Unit>() {
        override fun createResource() = Unit
        override fun configureResource(resource: Unit) = Unit
        override fun destroyResource(resource: Unit) {
            if (failDestroy) throw IllegalStateException("destroy failed")
        }

        override fun createApi(resource: Unit) = Unit
        override fun startTransaction(resource: Unit) = Unit
        override fun commitTransaction(resource: Unit) = Unit
        override fun rollbackTransaction(resource: Unit) {
            if (failRollback) throw IllegalStateException("rollback failed")
        }

        override fun canRetry(e: Exception) = e is Retryable
        override fun retryBackoff(attempt: Int): Duration? = if (attempt < 3) Duration.ZERO else null
    }

    @Test
    fun `a failing rollback doesn't hide the error that caused it`() {
        val cause = IllegalArgumentException("work failed")

        val thrown = assertFailsWith<IllegalArgumentException> { FlakyCleanup(failRollback = true).perform { throw cause } }

        assertSame(cause, thrown)
        assertEquals(listOf("rollback failed"), thrown.suppressed.map { it.message })
    }

    @Test
    fun `a failing rollback doesn't stop a retryable failure being retried`() {
        var attempts = 0

        val result = FlakyCleanup(failRollback = true).perform { if (++attempts == 1) throw Retryable() else "done" }

        assertEquals("done", result)
        assertEquals(2, attempts)
    }

    @Test
    fun `a failing cleanup doesn't hide the error the work raised`() {
        val cause = IllegalArgumentException("work failed")

        val thrown = assertFailsWith<IllegalArgumentException> { FlakyCleanup(failDestroy = true).perform { throw cause } }

        assertSame(cause, thrown)
        assertEquals(listOf("destroy failed"), thrown.suppressed.map { it.message })
    }

    @Test
    fun `a failing cleanup doesn't undo a committed result`() {
        assertEquals("done", FlakyCleanup(failDestroy = true).perform { "done" })
    }
}
