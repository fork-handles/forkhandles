package dev.forkhandles.result4k.kotest

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Disabled
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class MatchersTest {

    @Test
    fun `should correctly assert when Success`() {
        val value = "Successful"
        val success = Success(value)

        assertDoesNotThrow { success.shouldBeSuccess() }
        assertDoesNotThrow { success.shouldBeSuccess() shouldBe value }
        assertDoesNotThrow { success.shouldBeSuccess { assertEquals(value, it) } }
        assertDoesNotThrow { success shouldBeSuccess value }
    }

    @Test
    fun `should correctly assert when Failure`() {
        val reason = "Failed"
        val failure = Failure(reason)

        assertDoesNotThrow { failure.shouldBeFailure() }
        assertDoesNotThrow { failure.shouldBeFailure() shouldBe reason }
        assertDoesNotThrow { failure.shouldBeFailure { assertEquals(reason, it) } }
        assertDoesNotThrow { failure shouldBeFailure reason }
    }

    @Test
    fun `should correctly assert when Success but expecting Failure`() {
        val value = "Test successful"
        val success = Success(value)

        throwsAssertionError("expected:<Failure> but was:<Success(value=Test successful)>") {
            success.shouldBeFailure()
        }
        throwsAssertionError("expected:<Failure> but was:<Success(value=Test successful)>") {
            success.shouldBeFailure { }
        }
        throwsAssertionError("expected:<Failure(reason=Test successful)> but was:<Success(value=Test successful)>") {
            success shouldBeFailure value
        }
    }

    @Test
    fun `should correctly assert when Failure but expecting Success`() {
        val reason = "Test failed"
        val failure = Failure(reason)

        throwsAssertionError("expected:<Success> but was:<Failure(reason=Test failed)>") {
            failure.shouldBeSuccess()
        }
        throwsAssertionError("expected:<Success> but was:<Failure(reason=Test failed)>") {
            failure.shouldBeSuccess { }
        }
        throwsAssertionError("expected:<Success(value=Test failed)> but was:<Failure(reason=Test failed)>") {
            failure shouldBeSuccess reason
        }
    }

    @Test
    fun `should correctly assert value`() {
        throwsAssertionError("expected:<Failure(reason=Expected value)> but was:<Failure(reason=Actual value)>") {
            Failure("Actual value") shouldBeFailure "Expected value"
        }
        throwsAssertionError("expected:<Success(value=Expected value)> but was:<Success(value=Actual value)>") {
            Success("Actual value") shouldBeSuccess "Expected value"
        }
    }

    @Test
    @Disabled // Comment out to check it manually
    fun `check IntelliJ is showing diff window`() {
        Success("Actual value") shouldBeSuccess "Expected value"
    }

    private fun throwsAssertionError(message: String, block: () -> Unit) =
        assertThrows<AssertionError> { block() }.also {
            assertThat(it.message, present(equalTo(message)))
        }
}
