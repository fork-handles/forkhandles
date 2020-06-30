package dev.forkhandles.time

import java.time.Duration
import java.time.Duration.ofSeconds
import java.time.Instant
import java.time.Instant.EPOCH
import java.time.temporal.ChronoUnit.NANOS
import java.time.temporal.TemporalAmount
import java.util.concurrent.atomic.AtomicReference

/**
 * Clock whose underlying time is controllable, which is a core tenet of testing with time.
 */
interface TickableTimeSource : TimeSource {
    /**
     * Advance the underlying time by a custom amount, or the default amount if no value is passed.
     */
    fun tick(amount: Duration? = null): TickableTimeSource
}

object TestTimeSources {

    /**
     * TimeSource that always returns a fixed time unless advanced with tick().
     */
    fun Fixed(
        time: Instant = EPOCH,
        tick: Duration = ofSeconds(1)
    ) = object : TickableTimeSource {

        init {
            tick.requirePositive()
        }

        private val initial = AtomicReference(time)

        override fun tick(amount: Duration?) = apply {
            amount?.requirePositive()
            initial.apply { set(get() + (amount ?: tick)) } }

        private fun Duration.requirePositive() =
            require(!isNegative) { "Time can only tick forwards, not by $this" }

        override operator fun invoke() = initial.get()
    }

    /**
     * TimeSource that automatically ticks by a standard amount (default to 1s) when queried.
     */
    fun AutoTicking(start: Instant = EPOCH, tickSize: Duration = ofSeconds(1)): TickableTimeSource {
        val fixed = Fixed(start, tickSize)
        return object : TickableTimeSource by fixed {
            override operator fun invoke() = fixed().also { fixed.tick() }
        }
    }
}