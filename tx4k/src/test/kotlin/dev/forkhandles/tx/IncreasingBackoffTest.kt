package dev.forkhandles.tx

import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertEquals


class IncreasingBackoffTest {
    @Test
    fun `total backoff`() {
        val policy = increasingBackoff(Duration.ofSeconds(1))
        
        assertEquals(Duration.ofSeconds(1), totalBackoff(policy, 1), "attempt 1")
        assertEquals(Duration.ofSeconds(3), totalBackoff(policy, 2), "attempt 2")
        assertEquals(Duration.ofSeconds(6), totalBackoff(policy, 3), "attempt 3")
        assertEquals(Duration.ofSeconds(10), totalBackoff(policy, 4), "attempt 4")
        assertEquals(Duration.ofSeconds(15), totalBackoff(policy, 5), "attempt 5")
    }
    
    private fun totalBackoff(policy: RetryPolicy, maxAttempt: Int): Duration =
        (1..maxAttempt)
            .fold(Duration.ZERO) { acc, attempt -> acc + policy(attempt) }
}
