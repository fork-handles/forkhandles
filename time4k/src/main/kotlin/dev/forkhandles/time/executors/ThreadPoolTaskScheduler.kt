package dev.forkhandles.time.executors

import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.Executors
import java.util.concurrent.Future
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * This is an Task Scheduler backed by a real Scheduled thread pool. For production use.
 */
class ThreadPoolTaskScheduler(size: Int) : TaskScheduler {
    private val executor = Executors.newScheduledThreadPool(size)

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
