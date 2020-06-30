package dev.forkhandles.time

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.time.TimeSources.SystemUTC
import dev.forkhandles.time.TimeSources.Ticking
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.ZonedDateTime.now
import java.time.temporal.ChronoUnit.DAYS
import java.time.temporal.ChronoUnit.HOURS
import java.time.temporal.ChronoUnit.MINUTES
import java.time.temporal.ChronoUnit.SECONDS

class TimeSourceTest {

    @Test
    fun `utc timesource`() {
        assertThat(SystemUTC().truncatedTo(SECONDS),
            equalTo(now(ZoneId.of("UTC")).truncatedTo(SECONDS).toInstant()))
    }

    @Test
    fun `ticking timesource`() {
        assertThat(Ticking(SECONDS)(), equalTo(now(ZoneId.of("UTC")).truncatedTo(SECONDS).toInstant()))
        assertThat(Ticking(MINUTES)(), equalTo(now(ZoneId.of("UTC")).truncatedTo(MINUTES).toInstant()))
        assertThat(Ticking(HOURS)(), equalTo(now(ZoneId.of("UTC")).truncatedTo(HOURS).toInstant()))
        assertThat(Ticking(DAYS)(), equalTo(now(ZoneId.of("UTC")).truncatedTo(DAYS).toInstant()))
    }
}
