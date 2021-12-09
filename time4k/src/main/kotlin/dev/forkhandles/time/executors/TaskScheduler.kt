package dev.forkhandles.time.executors

import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.Future
import java.util.concurrent.ScheduledFuture

/**
 * Simpler interface for threaded tasks.
 */
interface TaskScheduler {
    fun <T> schedule(callable: Callable<T>, delay: Duration): ScheduledFuture<T>

    fun schedule(runnable: Runnable, delay: Duration): ScheduledFuture<*>

    fun scheduleWithFixedDelay(runnable: Runnable, initialDelay: Duration, delay: Duration): ScheduledFuture<*>

    fun scheduleAtFixedRate(runnable: Runnable, initialDelay: Duration, period: Duration): ScheduledFuture<*>

    fun shutdown()

    fun isShutdown(): Boolean

    fun submit(task: Runnable): Future<*>

    fun <T> submit(task: Callable<T>): Future<T>
}
