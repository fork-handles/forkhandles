package dev.forkhandles.time.executors

import java.time.Duration
import java.util.PriorityQueue
import java.util.Queue
import java.util.concurrent.Callable
import java.util.concurrent.CancellationException
import java.util.concurrent.Delayed
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.function.Consumer

/**
 * This is an controllable Task Scheduler where we can control the passage of time. For use in tests
 * to deterministically executions.
 */
class TaskListControllableTaskScheduler : ControllableTaskScheduler {

    private var clock = 0L
    private var isShutdown = false
    private var tasks = emptyTaskList()

    private fun emptyTaskList(): Queue<SimpleScheduleTask<*>> =
        PriorityQueue(Comparator.comparingLong { tasks -> tasks.getDelay(TimeUnit.MILLISECONDS) })

    private open inner class SimpleScheduleTask<T>(
        private val callable: Callable<T>,
        private val period: Duration?,
        val timeToRun: Long
    ) :
        ScheduledFuture<T> {
        private var isCancelled = false
        private var isDone = false
        private var result: T? = null
        private var error: Throwable? = null

        val isPeriodic: Boolean
            get() = period != null

        constructor(callable: Callable<T>, timeToRun: Long) : this(callable, null, timeToRun) {}

        override fun getDelay(unit: TimeUnit): Long = unit.convert(timeToRun - clock, TimeUnit.MILLISECONDS)

        override fun compareTo(other: Delayed): Int {
            throw UnsupportedOperationException("james didn't write")
        }

        override fun isDone(): Boolean = isDone

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
            throw IllegalStateException("get() before task runs")
        }

        @Throws(InterruptedException::class, ExecutionException::class, TimeoutException::class)
        override fun get(timeout: Long, unit: TimeUnit): T? {
            if (isCancelled) throw CancellationException("get(timeout) on cancelled task")
            if (error != null) throw ExecutionException(error)
            if (isDone) return result
            throw TimeoutException(
                "task not scheduled to run for another " + Duration.ofMillis(
                    timeToRun - clock
                )
            )
        }

        fun execute(exceptioner: Consumer<Throwable>) {
            try {
                if (!isCancelled) {
                    result = callable.call()
                }
            } catch (e: Exception) {
                error = e
                exceptioner.accept(e)
            } finally {
                isDone = true
            }
        }

        fun atNextExecutionTimeAfter(clock: Long): SimpleScheduleTask<T> {
            return SimpleScheduleTask(
                callable, period, clock + period!!.toMillis()
            )
        }
    }

    override fun submit(task: Runnable): Future<*> {
        try {
            task.run()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return SimpleFuture {}.apply { get() }
    }

    override fun <T> submit(task: Callable<T>): Future<T> {
        val result = task.call()
        return SimpleFuture { result }.apply { get() }
    }

    override fun isShutdown(): Boolean = isShutdown

    override fun <T> schedule(callable: Callable<T>, delay: Duration): ScheduledFuture<T> =
        enqueue(SimpleScheduleTask(callable, clock + delay.toMillis()))

    private fun <T> enqueue(task: SimpleScheduleTask<T>): ScheduledFuture<T> {
        if (!isShutdown) tasks.add(task)
        return task
    }

    override fun schedule(runnable: Runnable, delay: Duration): ScheduledFuture<*> =
        enqueue(SimpleScheduleTask<Any>({ runnable.run() }, clock + delay.toMillis()))

    override fun scheduleWithFixedDelay(
        runnable: Runnable,
        initialDelay: Duration,
        delay: Duration
    ): ScheduledFuture<*> =
        enqueue(
            SimpleScheduleTask<Unit>(
                { runnable.run() },
                delay,
                clock + initialDelay.toMillis()
            )
        )

    override fun scheduleAtFixedRate(runnable: Runnable, initialDelay: Duration, period: Duration): ScheduledFuture<*> =
        enqueue(
            SimpleScheduleTask<Unit>(
                { runnable.run() },
                period,
                clock + initialDelay.toMillis()
            )
        )

    override fun shutdown() {
        isShutdown = true
    }

    fun clear() {
        tasks.clear()
    }

    override fun timePasses(duration: Duration) {

        val durationMillis = duration.toMillis()
        val endOfPeriod = clock + durationMillis

        var count = 0

        while (true) {
            val ran = runNextTask(endOfPeriod)
            if (ran) {
                count++
                if (count % 10 == 0) {
                    println("Note: Executor executed $count tasks in a single timeslice")
                }
            }
            if (!ran) break
        }
        clock = endOfPeriod
    }

    private fun runNextTask(endOfPeriod: Long): Boolean {

        val nextTasks = emptyTaskList()
        var ranSomething = false

        val currentTasks = tasks.toList()

        tasks.clear()

        for (task in currentTasks) {
            if (task.isCancelled) continue
            val executionTimeOfTask = task.timeToRun
            if (executionTimeOfTask <= endOfPeriod) {
                clock = executionTimeOfTask
                ranSomething = true
                var threw = false
                task.execute { threw = true; }
                if (task.isPeriodic && !threw) {
                    nextTasks.add(task.atNextExecutionTimeAfter(executionTimeOfTask))
                }
            } else {
                nextTasks.add(task)
            }
        }
        tasks.addAll(nextTasks)
        return ranSomething
    }
}
