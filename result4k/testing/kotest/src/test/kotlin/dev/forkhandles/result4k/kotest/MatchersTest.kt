package dev.forkhandles.result4k.kotest

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class MatchersTest {

    @Test
    fun `should fail with correct message on Failure when expecting Success`() {
        val expectedValue = "Test failed"
        val result = Failure(expectedValue)

        assertThrows<AssertionError>("Failure(reason=Test failed) should be Success") {
            result.shouldBeSuccess()
        }

        assertThrows<AssertionError>("Failure(reason=Test failed) should be Success") {
            result.shouldBeSuccess { }
        }

        assertThrows<AssertionError>("Failure(reason=Test failed) should be Success(value=Test failed)") {
            result.shouldBeSuccess(
                expectedValue
            )
        }
    }

    @Test
    fun `should fail with correct message on Success when expecting Failure`() {
        val expectedValue = "Test successful"
        val result = Success(expectedValue)

        assertThrows<AssertionError>("Success(value=Test successful) should be Failuree") {
            result.shouldBeFailure()
        }

        assertThrows<AssertionError>("Failure(reason=Test failed) should be Success") {
            result.shouldBeFailure { }
        }

        assertThrows<AssertionError>("Failure(reason=Test failed) should be Success(value=Test failed)") {
            result.shouldBeFailure(
                expectedValue
            )
        }
    }
}
