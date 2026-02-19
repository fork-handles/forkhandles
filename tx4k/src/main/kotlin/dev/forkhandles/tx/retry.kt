package dev.forkhandles.tx

import java.time.Duration
import kotlin.random.Random

typealias RetryPolicy = (attempt: Int) -> Duration?

/**
 * A very simple backoff strategy that always backs off the same amount of time.
 * This is not suitable for production workloads.
 */
fun linearBackoff(backoff: Duration): RetryPolicy =
    fun(attempt: Int) =
        backoff

/**
 * A backoff strategy that increases the retry delay with each attempt.
 */
fun increasingBackoff(step: Duration): RetryPolicy =
    fun(attempt: Int) =
        step.multipliedBy(attempt.toLong())

/**
 * Limit the number of retries.
 */
fun RetryPolicy.maxAttempts(max: Int): RetryPolicy =
    fun(attempt: Int): Duration? {
        require(attempt > 0) { "attempt must be > 0" }
        return if (attempt <= max) this(attempt) else null
    }

/**
 * Applies jitter to a retry policy.
 */
fun RetryPolicy.withAdditiveJitter(maxFraction: Double = 0.1, random: Random = Random.Default): RetryPolicy =
    fun(attempt: Int): Duration? =
        this(attempt)?.let { baseDelay ->
            val jitterScale = maxFraction * random.nextDouble(-1.0, 1.0)
            val jitter = baseDelay.toMillis() * jitterScale
            baseDelay + Duration.ofMillis(jitter.toLong())
        }
