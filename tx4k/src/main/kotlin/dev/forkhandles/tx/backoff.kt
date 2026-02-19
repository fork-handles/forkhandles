package dev.forkhandles.tx

import java.time.Duration

/**
 * A very simple backoff strategy that is not suitable for production workloads.
 */
fun linearBackoff(step: Duration) =
    fun (attempt: Int) =
        step.multipliedBy(attempt.toLong())
