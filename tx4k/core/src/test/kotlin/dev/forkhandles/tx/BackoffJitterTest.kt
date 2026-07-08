package dev.forkhandles.tx

import java.time.Duration
import kotlin.test.Test
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class BackoffJitterTest {
    @Test
    fun `jittered policy adds jitter to base delay`() {
        val baseDelay = Duration.ofSeconds(1)
        val jitteredPolicy = linearBackoff(baseDelay).withAdditiveJitter(0.125)
        
        repeat(1000) {
            val jitteredDelay = assertNotNull(jitteredPolicy(it))
            assertTrue(jitteredDelay in (Duration.ofMillis(875) .. Duration.ofMillis(1125)))
        }
    }
    
    @Test
    fun `jittered policy signals end of retry`() {
        val jitteredPolicy = { _ : Int -> null }.withAdditiveJitter(0.125)
        
        assertNull(jitteredPolicy(1))
    }
}
