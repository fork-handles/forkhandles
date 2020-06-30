package dev.forkhandles.time

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.time.TestTimeSources.AutoTicking
import dev.forkhandles.time.TestTimeSources.Fixed
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.Instant

class TestTimeSourcesTest {

    @Test
    fun `fixed TimeSource`() {
        val now = Instant.now()
        val tick = Duration.ofSeconds(1)

        val fixed = Fixed(now, tick)
        assertThat(fixed(), equalTo(now))
        assertThat(fixed.tick(tick)(), equalTo(now + tick))
    }

    @Test
    fun `auto-ticking TimeSource`() {
        val now = Instant.now()
        val timesource = AutoTicking(now)
        val tick = Duration.ofSeconds(1)

        assertThat(timesource(), equalTo(now))
        assertThat(timesource.tick(tick)(), equalTo(now + tick + tick))
    }
}