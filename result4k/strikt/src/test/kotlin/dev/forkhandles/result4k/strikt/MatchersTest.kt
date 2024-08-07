package dev.forkhandles.result4k.strikt

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo

class MatchersTest {

    @Test
    fun `should correctly assert when Success`() {
        val actualValue = "Successful"
        val actualResult = Success(actualValue)

        assertDoesNotThrow { expectThat(actualResult).isSuccess() }
        assertDoesNotThrow { expectThat(actualResult).isSuccess<String>() }
        assertDoesNotThrow { expectThat(actualResult).isSuccess(actualValue) }
    }

    @Test
    fun `should correctly assert when Failure`() {
        val actualValue = "Failed"
        val actualResult = Failure(actualValue)

        assertDoesNotThrow { expectThat(actualResult).isFailure() }
        assertDoesNotThrow { expectThat(actualResult).isFailure<String>() }
        assertDoesNotThrow { expectThat(actualResult).isFailure(actualValue) }
    }

    @Test
    fun `should correctly assert when Success but expecting Failure`() {
        val actualValue = "Test successful"
        val actualResult = Success(actualValue)
        val expectedMessage =
            "Expect that Success(value=Test successful) is an instance of dev.forkhandles.resultk.Failure found dev.forkhandles.resultk.Success"

        expectAssertionError(expectedMessage) {
            expectThat(actualResult).isFailure()
        }

        expectAssertionError(expectedMessage) {
            expectThat(actualResult).isFailure<String>()
        }

        expectAssertionError(expectedMessage) {
            expectThat(actualResult).isFailure(actualValue)
        }
    }

    @Test
    fun `should correctly assert when Failure but expecting Success`() {
        val actualValue = "Test failed"
        val actualResult = Failure(actualValue)
        val expectedMessage =
            "Expect that Failure(reason=Test failed) is an instance of dev.forkhandles.resultk.Success found dev.forkhandles.resultk.Failure"

        expectAssertionError(expectedMessage) {
            expectThat(actualResult).isSuccess()
        }

        expectAssertionError(expectedMessage) {
            expectThat(actualResult).isSuccess<String>()
        }

        expectAssertionError(expectedMessage) {
            expectThat(actualResult).isSuccess(actualValue)
        }
    }

    private fun expectAssertionError(message: String, block: () -> Unit) =
        expectThrows<AssertionError> { block() }
            .and { get { subject.formatterMessage }.isEqualTo(message) }

    private val AssertionError.formatterMessage
        get() =
            message
                ?.replace("[^\\p{L}.()\\[\\] =]+".toRegex(), "")
                ?.replace(" +".toRegex(), " ")
                ?.trim()
                .also { println(it) }
}
