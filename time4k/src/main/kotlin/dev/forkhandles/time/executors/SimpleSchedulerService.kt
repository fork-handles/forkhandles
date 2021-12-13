package dev.forkhandles.time.executors

import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * This is a SimpleScheduler backed by a ScheduledExecutorService. For production use.
 */
class SimpleSchedulerService(private val executor: ScheduledExecutorService) : SimpleScheduler {

    constructor(threads: Int): this(Executors.newScheduledThreadPool(threads))

    override fun <T> schedule(callable: Callable<T>, delay: Duration): ScheduledFuture<T> =
        executor.schedule(callable, delay.toMillis(), TimeUnit.MILLISECONDS)

    override fun schedule(runnable: Runnable, delay: Duration): ScheduledFuture<*> =
        executor.schedule(runnable, delay.toMillis(), TimeUnit.MILLISECONDS)

    override fun scheduleWithFixedDelay(
        runnable: Runnable,
        initialDelay: Duration,
        delay: Duration
    ): ScheduledFuture<*> =
        executor.scheduleWithFixedDelay(runnable, initialDelay.toMillis(), delay.toMillis(), TimeUnit.MILLISECONDS)

    override fun scheduleAtFixedRate(runnable: Runnable, initialDelay: Duration, period: Duration): ScheduledFuture<*> =
        executor.scheduleAtFixedRate(runnable, initialDelay.toMillis(), period.toMillis(), TimeUnit.MILLISECONDS)

    override fun shutdown() = executor.shutdown()

    override fun isShutdown(): Boolean = executor.isShutdown

    override fun submit(task: Runnable): Future<*> = executor.submit(task)

    override fun <T> submit(task: Callable<T>): Future<T> = executor.submit(task)
}
