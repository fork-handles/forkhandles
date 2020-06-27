package dev.forkhandles.time

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit.DAYS
import java.time.temporal.ChronoUnit.HOURS
import java.time.temporal.ChronoUnit.MINUTES
import java.time.temporal.ChronoUnit.SECONDS

class ClokkTest {
    @Test
    fun `utc clock`() {
        assertThat(Clokk.UTC.now().truncatedTo(SECONDS), equalTo(ZonedDateTime.now(ZoneId.of("UTC")).truncatedTo(SECONDS)))
    }

    @Test
    fun `truncating clock`() {
        assertThat(Clokk.Truncated(SECONDS).now(), equalTo(ZonedDateTime.now(ZoneId.of("UTC")).truncatedTo(SECONDS)))
        assertThat(Clokk.Truncated(MINUTES).now(), equalTo(ZonedDateTime.now(ZoneId.of("UTC")).truncatedTo(MINUTES)))
        assertThat(Clokk.Truncated(HOURS).now(), equalTo(ZonedDateTime.now(ZoneId.of("UTC")).truncatedTo(HOURS)))
        assertThat(Clokk.Truncated(DAYS).now(), equalTo(ZonedDateTime.now(ZoneId.of("UTC")).truncatedTo(DAYS)))
    }
}