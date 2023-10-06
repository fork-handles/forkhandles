package dev.forkhandles.result4k.kotest

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Assertions.assertEquals
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
        assertDoesNotThrow { success.shouldBeSuccess(value) }
    }

    @Test
    fun `should correctly assert when Failure`() {
        val reason = "Failed"
        val failure = Failure(reason)

        assertDoesNotThrow { failure.shouldBeFailure() }
        assertDoesNotThrow { failure.shouldBeFailure() shouldBe reason }
        assertDoesNotThrow { failure.shouldBeFailure { assertEquals(reason, it) } }
        assertDoesNotThrow { failure.shouldBeFailure(reason) }
    }

    @Test
    fun `should correctly assert when Success but expecting Failure`() {
        val value = "Test successful"
        val success = Success(value)

        throwsAssertionError("Success(value=Test successful) should be Failure") {
            success.shouldBeFailure()
        }
        throwsAssertionError("Success(value=Test successful) should be Failure") {
            success.shouldBeFailure { }
        }
        throwsAssertionError("Success(value=Test successful) should be Failure(reason=Test successful)") {
            success.shouldBeFailure(value)
        }
    }

    @Test
    fun `should correctly assert when Failure but expecting Success`() {
        val reason = "Test failed"
        val failure = Failure(reason)

        throwsAssertionError("Failure(reason=Test failed) should be Success") {
            failure.shouldBeSuccess()
        }
        throwsAssertionError("Failure(reason=Test failed) should be Success") {
            failure.shouldBeSuccess { }
        }
        throwsAssertionError("Failure(reason=Test failed) should be Success(value=Test failed)") {
            failure.shouldBeSuccess(reason)
        }
    }

    @Test
    fun `should correctly assert value`() {
        throwsAssertionError("Failure(reason=Actual value) should be Failure(reason=Expected value)") {
            Failure("Actual value").shouldBeFailure("Expected value")
        }
        throwsAssertionError("Success(value=Actual value) should be Success(value=Expected value)") {
            Success("Actual value").shouldBeSuccess("Expected value")
        }
    }

    private fun throwsAssertionError(message: String, block: () -> Unit) =
        assertThrows<AssertionError> { block() }.also {
            assertThat(it.message, present(equalTo(message)))
        }
}
