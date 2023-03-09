package dev.forkhandles.result4k.kotest

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MatchersTest {

    @Test
    fun `should fail with correct message on Failure when expecting Success`() {
        val expectedValue = "Test failed"
        val result = Failure(expectedValue)

        expectsAssertionError("Failure(reason=Test failed) should be Success") {
            result.shouldBeSuccess()
        }

        expectsAssertionError("Failure(reason=Test failed) should be Success") {
            result.shouldBeSuccess { }
        }

        expectsAssertionError("Failure(reason=Test failed) should be Success(value=Test failed)") {
            result.shouldBeSuccess(expectedValue)
        }
    }

    @Test
    fun `should fail with correct message on Success when expecting Failure`() {
        val expectedValue = "Test successful"
        val result = Success(expectedValue)

        expectsAssertionError("Success(value=Test successful) should be Failure") {
            result.shouldBeFailure()
        }

        expectsAssertionError("Success(value=Test successful) should be Failure") {
            result.shouldBeFailure { }
        }

        expectsAssertionError("Success(value=Test successful) should be Failure(reason=Test successful)") {
            result.shouldBeFailure(
                expectedValue
            )
        }
    }

    private fun expectsAssertionError(message: String, block: () -> Unit) =
        assertThrows<AssertionError> { block() }.also { assertThat(it.message, present(equalTo(message))) }
}
