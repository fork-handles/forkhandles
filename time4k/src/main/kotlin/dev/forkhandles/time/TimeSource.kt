package dev.forkhandles.time

import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneId.of
import java.time.ZonedDateTime
import java.time.ZonedDateTime.now
import java.time.temporal.ChronoUnit.SECONDS
import java.time.temporal.TemporalUnit

/**
 * Functional Clock interface (which is not an abstract class!)
 */
typealias TimeSource = () -> Instant

object TimeSources {
    @JvmField
    val SystemUTC: TimeSource = Clock.systemUTC()::instant

    /**
     * TimeSource that truncates down to the latest unit (defaulting to exact seconds).
     */
    @JvmStatic
    fun Ticking(unit: TemporalUnit = SECONDS): TimeSource = { now(of("UTC")).truncatedTo(unit).toInstant() }
}