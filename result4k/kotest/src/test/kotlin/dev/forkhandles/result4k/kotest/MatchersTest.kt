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
        val actualValue = "Successful"
        val actualResult = Success(actualValue)

        assertDoesNotThrow { actualResult.shouldBeSuccess() }
        assertDoesNotThrow { actualResult.shouldBeSuccess() shouldBe actualValue }
        assertDoesNotThrow { actualResult.shouldBeSuccess { assertEquals(actualValue, it) } }
        assertDoesNotThrow { actualResult.shouldBeSuccess(actualValue) }
    }

    @Test
    fun `should correctly assert when Failure`() {
        val actualValue = "Failed"
        val actualResult = Failure(actualValue)

        assertDoesNotThrow { actualResult.shouldBeFailure() }
        assertDoesNotThrow { actualResult.shouldBeFailure() shouldBe actualValue }
        assertDoesNotThrow { actualResult.shouldBeFailure { assertEquals(actualValue, it) } }
        assertDoesNotThrow { actualResult.shouldBeFailure(actualValue) }
    }

    @Test
    fun `should correctly assert when Success but expecting Failure`() {
        val actualValue = "Test successful"
        val actualResult = Success(actualValue)

        throwsAssertionError("Success(value=Test successful) should be Failure") {
            actualResult.shouldBeFailure()
        }

        throwsAssertionError("Success(value=Test successful) should be Failure") {
            actualResult.shouldBeFailure { }
        }

        throwsAssertionError("Success(value=Test successful) should be Failure(reason=Test successful)") {
            actualResult.shouldBeFailure(actualValue)
        }
    }

    @Test
    fun `should correctly assert when Failure but expecting Success`() {
        val actualValue = "Test failed"
        val actualResult = Failure(actualValue)

        throwsAssertionError("Failure(reason=Test failed) should be Success") {
            actualResult.shouldBeSuccess()
        }

        throwsAssertionError("Failure(reason=Test failed) should be Success") {
            actualResult.shouldBeSuccess { }
        }

        throwsAssertionError("Failure(reason=Test failed) should be Success(value=Test failed)") {
            actualResult.shouldBeSuccess(actualValue)
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
