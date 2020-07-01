package dev.forkhandles.time

import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneId.of
import java.time.ZoneId.systemDefault
import java.time.ZonedDateTime
import java.time.ZonedDateTime.now
import java.time.temporal.ChronoUnit.SECONDS
import java.time.temporal.TemporalUnit
import java.util.TimeZone
import java.util.concurrent.TimeUnit

/**
 * Functional Clock interface (which is not an abstract class!)
 */
typealias TimeSource = () -> Instant

val systemTime = Clock.systemUTC()::instant

fun TimeSource.ticking(unit: TemporalUnit = SECONDS, tz: ZoneId = systemDefault()) : TimeSource =
    fun() = this().atZone(tz).truncatedTo(unit).toInstant()
