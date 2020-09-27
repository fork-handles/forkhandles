package dev.forkhandles.time

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant

class AutoTickingTimeSourceTest {

    @Test
    fun `getting the time ticks by configured amount`() {
        val start = Instant.now()
        val tick = Duration.ofSeconds(2)

        val timeSource = AutoTickingTimeSource(start, tick)

        assertThat(timeSource(), equalTo(start))
        assertThat(timeSource(), equalTo(start + tick))
        assertThat(timeSource(), equalTo(start + tick + tick))
    }

    @Test
    fun `ticked by configured amount`() {
        val now = Instant.now()
        val tick = Duration.ofSeconds(1)

        val timeSource = AutoTickingTimeSource(now, tick)

        assertThat(timeSource(), equalTo(now))

        timeSource.tick()

        assertThat(timeSource(), equalTo(now + tick + tick))
    }

    @Test
    fun `ticked by explicit amount`() {
        val now = Instant.ofEpochSecond(1)
        val configuredTick = Duration.ofSeconds(10)
        val explicitTick = Duration.ofSeconds(1)

        val fixed = AutoTickingTimeSource(now, configuredTick)

        assertThat(fixed(), equalTo(now))

        fixed.tick(explicitTick)

        assertThat(fixed(), equalTo(now + configuredTick + explicitTick))
        assertThat(fixed(), equalTo(now + configuredTick + explicitTick + configuredTick))
    }
}
