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
    fun `ticking clock`() {
        val now = ZonedDateTime.now()
        assertThat(TestClokk.Fixed(now).now(), equalTo(now))
    }

    @Test
    fun `ticking2 clock`() {
        val now = ZonedDateTime.now()
        val klock = TestClokk.AutoTicking(now)
        assertThat(klock.now(), equalTo(now))
        assertThat(klock.now(), equalTo(now.plusSeconds(1)))
    }

}