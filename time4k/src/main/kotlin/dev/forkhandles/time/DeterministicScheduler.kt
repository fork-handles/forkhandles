package dev.forkhandles.time

import java.time.Duration
import java.time.Instant
import java.util.concurrent.Callable
import java.util.concurrent.Delayed
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.SECONDS
import java.util.concurrent.TimeoutException

/**
 * A [ScheduledExecutorService] that executes commands on the thread that calls
 * [runNextPendingCommand][.runNextPendingCommand], [runUntilIdle][.runUntilIdle] or
 * [tick][.tick].  Objects of this class can also be used as
 * [java.util.concurrent.Executor]s or [java.util.concurrent.ExecutorService]s if
 * you just want to control background execution and don't need to schedule commands.
 */
class DeterministicScheduler(startTime: Instant = Instant.now()) : ScheduledExecutorService {
    private var head: ScheduledTask<*>? = null
    private var currentTime = startTime

    /**
     * Simulated current time that progresses as the scheduler is ticked.
     *
     * You can use a reference to this method as a [TimeSource]
     */
    fun currentTime() = currentTime


    /**
     * Runs time forwards by a given duration, executing any commands scheduled for
     * execution during that time period, and any background tasks spawned by the
     * scheduled tasks.  Therefore, when a call to tick returns, the executor
     * will be idle.
     */
    fun tick(duration: Duration) {
        var remaining = duration
        var head = this.head

        while (head != null && head.delay <= remaining) {
            remaining -= head.delay
            currentTime += head.delay
            head.delay = Duration.ZERO

            runNextPendingCommand()

            head = this.head
        }

        head?.apply { delay -= remaining }
        currentTime += remaining
    }

    fun tick(duration: Long, timeUnit: TimeUnit) {
        tick(duration(duration, timeUnit))
    }

    /**
     * Runs all commands scheduled to be executed immediately but does
     * not tick time forward.
     */
    fun runUntilIdle() {
        while (!isIdle()) {
            runNextPendingCommand()
        }
    }

    /**
     * Runs the next command scheduled to be executed immediately.
     */
    fun runNextPendingCommand() {
        val task = pop()

        task.run()

        if (!task.isCancelled && task.repeatDelay != null) {
            task.delay = task.repeatDelay
            add(task)
        }
    }

    private fun pop(): ScheduledTask<*> {
        val head = this.head

        check(head != null) { "cannot pop from an empty schedule" }
        check(head.delay <= Duration.ZERO) { "cannot pop a task when it has a non-zero delay" }

        this.head = head.next
        return head
    }


    /**
     * Reports whether scheduler is "idle": has no commands pending immediate execution.
     *
     * @return true if there are no commands pending immediate execution,
     * false if there are commands pending immediate execution.
     */
    fun isIdle(): Boolean =
        head.let { it == null || it.delay > Duration.ZERO }

    override fun execute(command: Runnable) {
        schedule(command, 0, SECONDS)
    }

    override fun schedule(command: Runnable, delay: Long, unit: TimeUnit): ScheduledFuture<*> =
        schedule(command.asCallable(), delay, unit)

    override fun <V> schedule(callable: Callable<V>, delay: Long, unit: TimeUnit): ScheduledFuture<V> =
        ScheduledTask(command = callable, delay = duration(delay, unit))
            .also { add(it) }


    override fun scheduleAtFixedRate(
        command: Runnable,
        initialDelay: Long,
        period: Long,
        unit: TimeUnit
    ): ScheduledFuture<*> = scheduleWithFixedDelay(command, initialDelay, period, unit)

    override fun scheduleWithFixedDelay(
        command: Runnable,
        initialDelay: Long,
        delay: Long,
        unit: TimeUnit
    ): ScheduledFuture<*> =
        ScheduledTask(
            command = command.asCallable(),
            delay = duration(initialDelay, unit),
            repeatDelay = duration(delay, unit)
        )
            .also { add(it) }

    @Throws(InterruptedException::class)
    override fun awaitTermination(timeout: Long, unit: TimeUnit): Boolean {
        blockingOperationsNotSupported()
    }

    @Throws(InterruptedException::class)
    override fun <T> invokeAll(tasks: Collection<Callable<T>?>): List<Future<T>> {
        blockingOperationsNotSupported()
    }

    @Throws(InterruptedException::class)
    override fun <T> invokeAll(tasks: Collection<Callable<T>?>, timeout: Long, unit: TimeUnit): List<Future<T>> {
        blockingOperationsNotSupported()
    }

    @Throws(InterruptedException::class, ExecutionException::class)
    override fun <T> invokeAny(tasks: Collection<Callable<T>?>): T {
        blockingOperationsNotSupported()
    }

    @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
    override fun <T> invokeAny(tasks: Collection<Callable<T>?>, timeout: Long, unit: TimeUnit): T {
        blockingOperationsNotSupported()
    }

    override fun isShutdown(): Boolean {
        shutdownNotSupported()
    }

    override fun isTerminated(): Boolean {
        shutdownNotSupported()
    }

    override fun shutdown() {
        shutdownNotSupported()
    }

    override fun shutdownNow(): List<Runnable> {
        shutdownNotSupported()
    }

    override fun <T> submit(callable: Callable<T>) = schedule(callable, 0, SECONDS)

    override fun submit(command: Runnable): Future<*> = submit<Any?>(command, null)

    override fun <T> submit(command: Runnable, result: T) = submit(CallableRunnableAdapter(command, result))

    private class CallableRunnableAdapter<T>(private val runnable: Runnable, private val result: T) : Callable<T> {
        override fun toString() = runnable.toString()

        @Throws(Exception::class)
        override fun call(): T {
            runnable.run()
            return result
        }
    }

    private fun Runnable.asCallable(): Callable<Nothing?> =
        Callable {
            run()
            null
        }

    private inner class ScheduledTask<T>(
        val command: Callable<T>,
        var delay: Duration,
        val repeatDelay: Duration? = null
    ) : ScheduledFuture<T>, Runnable {
        var next: ScheduledTask<*>? = null

        private var isCancelled = false
        private var isDone = false
        private var futureResult: T? = null
        private var failure: Exception? = null

        override fun toString(): String = "$command repeatDelay=$repeatDelay"

        override fun getDelay(unit: TimeUnit) =
            delayOf(this)?.let(unit::convert) ?: -1

        override fun compareTo(other: Delayed): Nothing =
            throw UnsupportedOperationException("not supported")

        override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
            isCancelled = true
            return remove(this)
        }

        override fun get(): T? {
            if (!isDone) {
                blockingOperationsNotSupported()
            }
            if (failure != null) {
                throw ExecutionException(failure)
            }
            return futureResult
        }

        override fun get(timeout: Long, unit: TimeUnit): T? =
            get()

        override fun isCancelled(): Boolean = isCancelled

        override fun isDone(): Boolean = isDone

        override fun run() {
            try {
                futureResult = command.call()
            } catch (e: Exception) {
                failure = e
            }
            isDone = true
        }
    }

    private fun add(newTask: ScheduledTask<*>) {
        var prev: ScheduledTask<*>? = null
        var next = head

        while (next != null && next.delay <= newTask.delay) {
            newTask.delay -= next.delay
            prev = next
            next = next.next
        }

        if (prev == null) {
            head = newTask
        } else {
            prev.next = newTask
        }

        if (next != null) {
            next.delay -= newTask.delay
            newTask.next = next
        }
    }

    private fun remove(element: ScheduledTask<*>): Boolean {
        var prev: ScheduledTask<*>? = null
        var node = head

        while (node != null && node !== element) {
            prev = node
            node = node.next
        }

        if (node == null) {
            return false
        }

        val next = node.next

        if (next != null) {
            next.delay += node.delay
        }

        if (prev == null) {
            head = node.next
        } else {
            prev.next = node.next
        }

        return true
    }

    private fun delayOf(element: ScheduledTask<*>): Duration? {
        var ret = Duration.ZERO
        var next = head
        while (next != null) {
            ret += next.delay
            if (next == element) {
                return ret
            }
            next = next.next
        }

        return null
    }


    private fun blockingOperationsNotSupported(): Nothing =
        throw UnsupportedSynchronousOperationException(
            "cannot perform blocking wait on a task scheduled on a " + javaClass.simpleName
        )

    private fun shutdownNotSupported(): Nothing =
        throw UnsupportedOperationException("shutdown not supported")

    private fun duration(delay: Long, unit: TimeUnit) =
        Duration.of(delay, unit.toChronoUnit())
}
