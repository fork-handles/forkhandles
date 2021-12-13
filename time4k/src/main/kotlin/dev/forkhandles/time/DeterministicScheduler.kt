package dev.forkhandles.time

import dev.forkhandles.time.executors.SimpleScheduler
import java.time.Duration
import java.time.Instant
import java.util.*
import java.util.concurrent.Callable
import java.util.concurrent.CancellationException
import java.util.concurrent.Delayed
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

/**
 * A [ScheduledExecutorService] that executes commands on the thread that calls
 * [runNextPendingCommand][.runNextPendingCommand], [runUntilIdle][.runUntilIdle] or
 * [tick][.tick].  Objects of this class can also be used as
 * [java.util.concurrent.Executor]s or [java.util.concurrent.ExecutorService]s if
 * you just want to control background execution and don't need to schedule commands.
 */
class DeterministicScheduler(startTime: Instant = Instant.now()) : ScheduledExecutorService, SimpleScheduler {

    private var clock = startTime
    private var isShutdown = false
    private var tasks = emptyTaskList()

    fun currentTime() = clock

    private fun emptyTaskList(): Queue<SimpleScheduleTask<*>> =
        PriorityQueue(Comparator.comparingLong { tasks -> tasks.getDelay(TimeUnit.MILLISECONDS) })


    override fun submit(task: Runnable): Future<*> {
        return schedule(task, Duration.ZERO)
    }

    override fun <T> submit(task: Callable<T>): Future<T> {
        return schedule(task, Duration.ZERO)
    }

    override fun isShutdown(): Boolean = isShutdown

    override fun <T> schedule(callable: Callable<T>, delay: Duration): ScheduledFuture<T> =
        enqueue(SimpleScheduleTask(callable, clock + delay))

    private fun <T> enqueue(task: SimpleScheduleTask<T>): ScheduledFuture<T> {
        if (!isShutdown) tasks.add(task)
        return task
    }

    override fun schedule(runnable: Runnable, delay: Duration): ScheduledFuture<*> =
        enqueue(
            SimpleScheduleTask(
                {
                    runnable.run()
                    null
                },
                clock + delay
            )
        )

    override fun scheduleWithFixedDelay(
        runnable: Runnable,
        initialDelay: Duration,
        delay: Duration
    ): ScheduledFuture<*> = enqueue(
        SimpleScheduleTask(
            { runnable.run() },
            delay,
            clock + initialDelay
        )
    )

    override fun scheduleAtFixedRate(runnable: Runnable, initialDelay: Duration, period: Duration): ScheduledFuture<*> =
        enqueue(
            SimpleScheduleTask<Unit>(
                { runnable.run() },
                period,
                clock + initialDelay
            )
        )

    override fun shutdown() {
        isShutdown = true
    }

    fun clear() {
        tasks.clear()
    }

    fun isIdle(): Boolean {
        return tasks.size == 0 || tasks.peek().timeToRun > clock
    }

    fun runUntilIdle() {
        tick(Duration.ZERO)
    }

    fun tick(quantity: Long, unit: TimeUnit) {
        tick(asDuration(quantity, unit))
    }

    fun tick(duration: Duration) {

        val endOfPeriod = clock + duration

        while (true) {
            if (!runNextTask(endOfPeriod)) break
        }
        clock = endOfPeriod
    }

    private fun runNextTask(endOfPeriod: Instant): Boolean {

        val nextTasks = emptyTaskList()
        var ranSomething = false
        var execute = true
        val currentTasks = tasks.toList()

        tasks.clear()

        for (task in currentTasks) {
            if (task.isCancelled) continue
            val executionTimeOfTask = task.timeToRun
            if (execute && executionTimeOfTask <= endOfPeriod) {
                clock = executionTimeOfTask
                ranSomething = true
                val success = task.execute()
                if (task.isPeriodic && success && !task.isCancelled) {
                    nextTasks.add(task.atNextExecutionTimeAfter(executionTimeOfTask))
                }

                if ( tasks.size > 0 ) {
                    // if a task added another task, then we need to drop out
                    // so that the added task runs in the correct order
                    execute = false
                }

            } else {
                nextTasks.add(task)
            }
        }
        tasks.addAll(nextTasks)
        return ranSomething
    }

    override fun <T : Any?> submit(task: Runnable, result: T): Future<T> {
        return schedule(Callable { task.run(); result }, Duration.ZERO)
    }

    override fun schedule(command: Runnable, delay: Long, unit: TimeUnit): ScheduledFuture<*> {
        return schedule(command, asDuration(delay, unit))
    }

    override fun <V : Any?> schedule(callable: Callable<V>, delay: Long, unit: TimeUnit): ScheduledFuture<V> {
        return schedule(callable, asDuration(delay, unit))
    }

    private fun asDuration(delay: Long, unit: TimeUnit) =
        Duration.ofMillis(TimeUnit.MILLISECONDS.convert(delay, unit))

    override fun scheduleWithFixedDelay(
        command: Runnable,
        initialDelay: Long,
        delay: Long,
        unit: TimeUnit
    ): ScheduledFuture<*> =
        scheduleWithFixedDelay(
            command,
            initialDelay = asDuration(initialDelay, unit),
            delay = asDuration(delay, unit)
        )

    override fun scheduleAtFixedRate(
        command: Runnable,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit
    ): ScheduledFuture<*> =
        scheduleAtFixedRate(
            command,
            initialDelay = asDuration(initialDelay, unit),
            period = asDuration(period, unit)
        )

    override fun execute(command: Runnable) {
        submit(command)
    }

    override fun shutdownNow(): List<Runnable> {
        shutdown()
        return listOf()
    }

    override fun isTerminated(): Boolean {
        return isShutdown
    }

    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean {
        if (isShutdown) {
            return true
        }
        blockingOperationsNotSupported()
    }

    override fun <T : Any?> invokeAll(tasks: MutableCollection<out Callable<T>>): MutableList<Future<T>> =
        blockingOperationsNotSupported()

    override fun <T : Any?> invokeAll(
        tasks: MutableCollection<out Callable<T>>,
        timeout: Long,
        unit: TimeUnit
    ): MutableList<Future<T>> =
        blockingOperationsNotSupported()

    override fun <T : Any?> invokeAny(tasks: MutableCollection<out Callable<T>>): T = blockingOperationsNotSupported()

    override fun <T : Any?> invokeAny(tasks: MutableCollection<out Callable<T>>, timeout: Long, unit: TimeUnit): T =
        blockingOperationsNotSupported()

    private inner class SimpleScheduleTask<T>(
        private val callable: Callable<T>,
        private val period: Duration?,
        val timeToRun: Instant
    ) :
        ScheduledFuture<T> {
        private var isCancelled = false
        private var isDone = false
        private var result: T? = null
        private var error: Throwable? = null

        init {
            period?.run { if (this <= Duration.ZERO) throw IllegalArgumentException("period/rate must be > 0") }
        }

        val isPeriodic: Boolean
            get() = period != null

        constructor(callable: Callable<T>, timeToRun: Instant) : this(callable, null, timeToRun) {}

        override fun getDelay(unit: TimeUnit): Long =
            unit.convert(timeToRun.toEpochMilli() - clock.toEpochMilli(), TimeUnit.MILLISECONDS)

        override fun compareTo(other: Delayed): Int {
            throw UnsupportedOperationException("james didn't write")
        }

        override fun isDone(): Boolean = isDone || isCancelled

        override fun isCancelled(): Boolean = isCancelled

        override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
            if (isDone) {
                return false
            }
            isCancelled = true
            return true
        }

        @Throws(InterruptedException::class, ExecutionException::class)
        override fun get(): T? {
            if (isCancelled) throw CancellationException("get() on cancelled task")
            if (error != null) throw ExecutionException(error)
            if (isDone) return result
            throw UnsupportedSynchronousOperationException(
                "task not scheduled to run for another " + Duration.ofMillis(
                    timeToRun.toEpochMilli() - clock.toEpochMilli()
                )
            )
        }

        @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
        override fun get(timeout: Long, unit: TimeUnit): T? {
            return get()
        }

        fun execute(): Boolean {
            return try {
                if (!isCancelled) {
                    result = callable.call()
                }
                true
            } catch (e: Exception) {
                error = e
                false
            } finally {
                isDone = true
            }
        }

        fun atNextExecutionTimeAfter(clock: Instant): SimpleScheduleTask<T> {
            return SimpleScheduleTask(
                callable, period, clock + period!!
            )
        }
    }

    private fun blockingOperationsNotSupported(): Nothing =
        throw UnsupportedSynchronousOperationException(
            "cannot perform blocking wait on a task scheduled on a " + javaClass.simpleName
        )
}
