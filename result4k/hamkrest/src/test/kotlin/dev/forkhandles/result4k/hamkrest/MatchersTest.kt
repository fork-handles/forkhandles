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

        throwsAssertionError("expected: a value that is Failure\nbut was: Success(value=Test successful)") {
            assertThat(actualResult, isFailure())
        }

        throwsAssertionError("expected: a value that is Failure(reason=Test successful)\nbut was: Success(value=Test successful)") {
            assertThat(actualResult, isFailure(actualValue))
        }
    }

    @Test
    fun `should correctly assert when Failure but expecting Success`() {
        val actualValue = "Test failed"
        val actualResult = Failure(actualValue)

        throwsAssertionError("expected: a value that is Success\nbut was: Failure(reason=Test failed)") {
            assertThat(actualResult, isSuccess())
        }

        throwsAssertionError("expected: a value that is Success(value=Test failed)\nbut was: Failure(reason=Test failed)") {
            assertThat(actualResult, isSuccess(actualValue))
        }
    }

    private fun throwsAssertionError(message: String, block: () -> Unit) =
        assertThrows<AssertionError> { block() }.also {
            assertThat(it.message, present(equalTo(message)))
        }

}
