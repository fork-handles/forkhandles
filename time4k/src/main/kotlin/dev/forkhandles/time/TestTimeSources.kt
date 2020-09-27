package dev.forkhandles.time

import java.time.Duration
import java.time.Duration.ofSeconds
import java.time.Instant
import java.time.Instant.EPOCH
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

/**
 * TimeSource that always returns a fixed time unless advanced with tick().
 */
class FixedTimeSource(
    time: Instant = EPOCH,
    private val tick: Duration = ofSeconds(1)
) : TickableTimeSource {

    init {
        tick.requirePositive()
    }

    private val initial = AtomicReference(time)

    override fun tick(amount: Duration?) = apply {
        amount?.requirePositive()
        initial.getAndUpdate { it + (amount ?: tick) }
    }

    private fun Duration.requirePositive() =
        require(!isNegative) { "Time can only tick forwards, not by $this" }

    override operator fun invoke(): Instant = initial.get()
}

/**
 * TimeSource that automatically ticks by a standard amount (default to 1s) when queried.
 */
class AutoTickingTimeSource(private val underlying: TickableTimeSource) : TickableTimeSource by underlying {
    constructor(time: Instant = EPOCH, tick: Duration = ofSeconds(1)) : this(FixedTimeSource(time, tick))

    override operator fun invoke() = underlying().also { underlying.tick() }
}
