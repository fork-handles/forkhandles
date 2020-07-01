package dev.forkhandles.time

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant

class FixedTimeSourceTest {
    @Test
    fun `ticked by configured amount`() {
        val start = Instant.now()
        val tick = Duration.ofSeconds(2)
        
        val timeSource = FixedTimeSource(start, tick)
        
        assertThat(timeSource(), equalTo(start))
        
        timeSource.tick()
        assertThat(timeSource(), equalTo(start + tick))
    }
    
    @Test
    fun `ticked by explicit amount`() {
        val now = Instant.now()
        val tick = Duration.ofSeconds(1)

        val fixed = FixedTimeSource(now, Duration.ofSeconds(10))
        assertThat(fixed(), equalTo(now))
        assertThat(fixed.tick(tick)(), equalTo(now + tick))
    }
    
    @Test
    fun `getting the time does not progress time`() {
        val now = Instant.now()
        val tick = Duration.ofSeconds(1)

        val timeSource = FixedTimeSource(now, Duration.ofSeconds(10))
        
        assertThat(timeSource(), equalTo(now))
        assertThat(timeSource(), equalTo(now))
        assertThat(timeSource(), equalTo(now))
    
        timeSource.tick(tick)
        
        assertThat(timeSource(), equalTo(now + tick))
        assertThat(timeSource(), equalTo(now + tick))
        assertThat(timeSource(), equalTo(now + tick))
    }
}
