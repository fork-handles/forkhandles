package dev.forkhandles.values

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.startsWith
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset.UTC
import kotlin.random.Random

class TimeOrderedIdsTest {

    private fun clockAt(epochMillis: Long) = Clock.fixed(Instant.ofEpochMilli(epochMillis), UTC)

    @Test
    fun `v7 uuid encodes the clock time as a version 7 rfc uuid`() {
        val uuid = UUIDValueFactory(::TV).v7(clockAt(1645557742000), Random(0)).value

        assertThat(uuid.toString(), startsWith("017f22e2-79b0-7"))
        assertThat(uuid.version(), equalTo(7))
        assertThat(uuid.variant(), equalTo(2))
    }
}
