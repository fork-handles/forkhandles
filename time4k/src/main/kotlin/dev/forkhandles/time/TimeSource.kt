package dev.forkhandles.time

import java.time.Clock
import java.time.Instant
import java.time.ZoneId
import java.time.ZoneOffset.UTC
import java.time.temporal.ChronoUnit.SECONDS
import java.time.temporal.TemporalUnit

/**
 * Functional Clock interface (which is not an abstract class!)
 */
typealias TimeSource = () -> Instant

val systemTime = Clock.systemUTC()::instant

fun TimeSource.ticking(unit: TemporalUnit = SECONDS, tz: ZoneId = UTC): TimeSource =
    fun() = this().atZone(tz).truncatedTo(unit).toInstant()
