package dev.forkhandles.time

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import java.time.Duration
import java.time.ZonedDateTime

class TestClokkTest {

    @Test
    fun `fixed clock`() {
        val now = ZonedDateTime.now()
        val tick = Duration.ofSeconds(1)

        val fixed = TestClokk.Fixed(now, tick)
        assertThat(fixed.now(), equalTo(now))
        assertThat(fixed.tick(tick).now(), equalTo(now + tick))
    }

    @Test
    fun `auto-ticking clock`() {
        val now = ZonedDateTime.now()
        val clokk = TestClokk.AutoTicking(now)
        val tick = Duration.ofSeconds(1)

        assertThat(clokk.now(), equalTo(now))
        assertThat(clokk.tick(tick).now(), equalTo(now + tick + tick))
    }
}