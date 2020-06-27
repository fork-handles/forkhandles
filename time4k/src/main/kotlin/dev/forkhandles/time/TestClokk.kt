package dev.forkhandles.time

import java.time.Duration.ofSeconds
import java.time.Instant.EPOCH
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.ZonedDateTime.ofInstant
import java.time.temporal.TemporalAmount
import java.util.concurrent.atomic.AtomicReference

/**
 * Clock whose underlying time is controllable, which is a core tenet of testing with time.
 */
interface TickableClokk : Clokk {
    /**
     * Advance the underlying time by a custom amount, or the default amount if no value is passed.
     */
    fun tick(amount: TemporalAmount? = null): TickableClokk
}

object TestClokk {

    /**
     * Clock that always returns a fixed time unless advanced with tick().
     */
    fun Fixed(
        time: ZonedDateTime = ofInstant(EPOCH, ZoneId.of("UTC")),
        tick: TemporalAmount = ofSeconds(1)
    ) = object : TickableClokk {
        private val initial = AtomicReference(time)

        override fun tick(amount: TemporalAmount?) = apply { initial.apply { set(get() + (amount ?: tick)) } }

        override fun now() = initial.get()
    }

    /**
     * Clock that automatically ticks by a standard amount (default to 1s) when queried.
     */
    fun AutoTicking(start: ZonedDateTime, tickSize: TemporalAmount = ofSeconds(1)): TickableClokk {
        val fixed = Fixed(start, tickSize)
        return object : TickableClokk by fixed {
            override fun now() = fixed.now().also { fixed.tick() }
        }
    }
}