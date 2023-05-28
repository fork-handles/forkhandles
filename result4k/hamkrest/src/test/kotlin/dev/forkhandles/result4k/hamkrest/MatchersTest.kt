package dev.forkhandles.result4k.hamkrest

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import org.junit.jupiter.api.assertThrows

class MatchersTest {

    @Test
    fun `should correctly assert when Success`() {
        val actualValue = "Successful"
        val actualResult = Success(actualValue)

        assertDoesNotThrow { assertThat(actualResult, isSuccess()) }
        assertDoesNotThrow { assertThat(actualResult, isSuccess(actualValue)) }
    }

    @Test
    fun `should correctly assert when Failure`() {
        val actualValue = "Failed"
        val actualResult = Failure(actualValue)

        assertDoesNotThrow { assertThat(actualResult, isFailure()) }
        assertDoesNotThrow { assertThat(actualResult, isFailure(actualValue)) }
    }

    @Test
    fun `should correctly assert when Success but expecting Failure`() {
        val actualValue = "Test successful"
        val actualResult = Success(actualValue)

        throwsAssertionError("expected: a value that is a Failure\nbut was: Success(value=Test successful)") {
            assertThat(actualResult, isFailure())
        }

        throwsAssertionError("expected: a value that is a Failure and has reason that is equal to \"Test successful\"\nbut was: Success(value=Test successful)") {
            assertThat(actualResult, isFailure(actualValue))
        }
    }

    @Test
    fun `should correctly assert when Failure but expecting Success`() {
        val actualValue = "Test failed"
        val actualResult = Failure(actualValue)

        throwsAssertionError("expected: a value that is a Success\nbut was: Failure(reason=Test failed)") {
            assertThat(actualResult, isSuccess())
        }

        throwsAssertionError("expected: a value that is a Success and has value that is equal to \"Test failed\"\nbut was: Failure(reason=Test failed)") {
            assertThat(actualResult, isSuccess(actualValue))
        }
    }

    @Test
    fun `should correctly assert when Success and expecting Success with inner matcher`() {
        val actualValue = "Test success"
        val actualResult = Success(actualValue)

        throwsAssertionError("expected: a value that is a Success and has value that is equal to \"Test Success\"\nbut had value that was: \"Test success\"") {
            assertThat(actualResult, isSuccess(equalTo("Test Success")))
        }
    }

    @Test
    fun `should correctly assert when Failure and expecting Failure with inner matcher`() {
        val actualValue = "Test failure"
        val actualResult = Failure(actualValue)

        throwsAssertionError("expected: a value that is a Failure and has reason that is equal to \"Test Failure\"\nbut had reason that was: \"Test failure\"") {
            assertThat(actualResult, isFailure(equalTo("Test Failure")))
        }
    }

    private fun throwsAssertionError(message: String, block: () -> Unit) =
        assertThrows<AssertionError> { block() }.also {
            assertThat(it.message, present(equalTo(message)))
        }

}
