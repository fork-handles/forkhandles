package dev.forkhandles.time

import java.time.ZoneId
import java.time.ZoneId.of
import java.time.ZonedDateTime
import java.time.ZonedDateTime.now
import java.time.temporal.ChronoUnit.SECONDS
import java.time.temporal.TemporalUnit

/**
 * Clock interface (not abstract class!) which operates in ZonedDateTimes
 * instead of zoneless Instants. This is preferable for consistency.
 */
interface Clokk {

    fun now(): ZonedDateTime

    companion object {
        @JvmStatic
        val UTC = zoned(of("UTC"))

        @JvmStatic
        fun zoned(zoneId: ZoneId) = object : Clokk {
            override fun now(): ZonedDateTime = now(zoneId)
        }

        /**
         * Clock that truncates down to the latest unit (defaulting to exact seconds).
         */
        @JvmStatic
        fun Truncated(unit: TemporalUnit = SECONDS, zoneId: ZoneId = of("UTC")) = object : Clokk {
            override fun now(): ZonedDateTime = now(zoneId).truncatedTo(unit)
        }
    }
}