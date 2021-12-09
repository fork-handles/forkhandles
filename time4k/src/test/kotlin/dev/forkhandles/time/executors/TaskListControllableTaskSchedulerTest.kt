package dev.forkhandles.time.executors

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.throws
import org.junit.jupiter.api.Test
import java.time.Duration
import java.util.concurrent.Callable
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class TaskListControllableTaskSchedulerTest {

    private var service = TaskListControllableTaskScheduler()
    private var counter = AtomicInteger()

    @Test
    fun isNotShutdownUntilItIs() {
        assertThat(service.isShutdown(), equalTo(false))
        service.shutdown()
        assertThat(service.isShutdown(), equalTo(true))
    }

    @Test
    fun tasksSubmittedAfterShutdownAreIgnored() {
        service.shutdown()
        service.schedule<Int>({ counter.incrementAndGet() }, Duration.ofSeconds(1))
        service.timePasses(Duration.ofSeconds(2))
        assertThat(counter.get(), equalTo(0))
    }

    @Test
    fun tasksAreRunAtTheRightTime() {
        service.schedule<Int>({ counter.incrementAndGet() }, Duration.ofSeconds(1))
        assertThat(counter.get(), equalTo(0))
        service.timePasses(Duration.ofMillis(999))
        assertThat(counter.get(), equalTo(0))
        service.timePasses(Duration.ofMillis(1))
        assertThat(counter.get(), equalTo(1))
    }

    @Test
    fun runsScheduledTasksThatAreSubmittedAtTheCorrectTime() {
        assertThat(counter.get(), equalTo(0))
        service.schedule<Int>({ counter.incrementAndGet() }, Duration.ofSeconds(1))
        assertThat(counter.get(), equalTo(0))
        service.timePasses(Duration.ofSeconds(1))
        assertThat(counter.get(), equalTo(1))
    }

    @Test
    fun schedulingARunnableRunsItAtTheRightTime() {
        service.schedule({ counter.incrementAndGet() }, Duration.ofDays(1))
        assertThat(counter.get(), equalTo(0))
        service.timePasses(Duration.ofDays(1))
        assertThat(counter.get(), equalTo(1))
    }

    @Test
    fun cancellingAScheduledTaskBeforeItRunsWillCancelIt() {
        val future = service.schedule<Int>({ counter.incrementAndGet() }, Duration.ofSeconds(1))
        future.cancel(true)
        service.timePasses(Duration.ofSeconds(1))
        assertThat(counter.get(), equalTo(0))
        assertThat(future.isCancelled, equalTo(true))
    }

    @Test
    fun gettingCancelledFuture() {
        assertThat({
            val future = service.schedule<Int>({ counter.incrementAndGet() }, Duration.ofSeconds(1))
            future.cancel(true)
            future.get()
        }, throws<CancellationException>())

    }

    @Test
    fun gettingCancelledFutureWithTimeout() {
        assertThat({
            val future = service.schedule<Int>({ counter.incrementAndGet() }, Duration.ofSeconds(1))
            future.cancel(true)
            future.get(1, TimeUnit.MILLISECONDS)
        }, throws<CancellationException>())
    }

    @Test
    fun canRetrieveTheResultOnceTheTaskHasRun() {
        val future = service.schedule<Int>({ counter.incrementAndGet() }, Duration.ofSeconds(1))
        service.timePasses(Duration.ofSeconds(1))
        assertThat(counter.get(), equalTo(1))
        assertThat(future.get(), equalTo(1))
        assertThat(future.get(1, TimeUnit.MILLISECONDS), equalTo(1))
        assertThat(future.isDone, equalTo(true))
    }

    @Test
    @Throws(Exception::class)
    fun schedulingTasksInDifferentOrders() {
        service.schedule<Int>({ counter.addAndGet(10) }, Duration.ofSeconds(10))
        service.schedule<Int>({ counter.incrementAndGet() }, Duration.ofSeconds(1))

        service.timePasses(Duration.ofSeconds(1))
        assertThat(counter.get(), equalTo(1))
    }

    @Test
    fun retrievingResultBeforeTaskHasRunThrows() {
        assertThat({
            val future = service.schedule<Int>({ counter.incrementAndGet() }, Duration.ofSeconds(1))
            future.get()
        }, throws<IllegalStateException>())
    }

    @Test
    fun retrievingResultWithTimeoutBeforeTaskRuns() {
        assertThat({
            val future = service.schedule<Int>({ counter.incrementAndGet() }, Duration.ofSeconds(1))
            future.get(1, TimeUnit.MILLISECONDS)
        }, throws<TimeoutException>())
    }

    @Test
    fun taskThrowingExceptionStillRunsOtherTasks() {
        service.schedule<Nothing>({ throw NullPointerException() }, Duration.ofSeconds(1))
        service.schedule<Int>({ counter.incrementAndGet() }, Duration.ofSeconds(1))
        service.timePasses(Duration.ofSeconds(1))
        assertThat(counter.get(), equalTo(1))
    }

    @Test
    fun periodicTaskThrowingExceptionSuppressesSubsequentExecutions() {
        service.scheduleAtFixedRate(
            { counter.incrementAndGet(); throw NullPointerException() },
            initialDelay = Duration.ZERO,
            period = Duration.ofSeconds(1)
        )
        service.timePasses(Duration.ofSeconds(3))
        assertThat(counter.get(), equalTo(1))
    }

    @Test
    fun retrievingResultOfFailedTask() {
        assertThat({
            val future = service.schedule<Nothing>({ throw NullPointerException() }, Duration.ofSeconds(1))
            service.timePasses(Duration.ofSeconds(1))
            future.get()
        }, throws<ExecutionException>())
    }

    @Test
    fun retrievingResultOfFailedTaskWithTimeout() {
        assertThat({
            val future = service.schedule<Nothing>({ throw NullPointerException() }, Duration.ofSeconds(1))
            service.timePasses(Duration.ofSeconds(1))

            future.get(1, TimeUnit.MILLISECONDS)
        }, throws<ExecutionException>())
    }

    @Test
    fun schedulingATaskAtFixedRateWillRunItAfterInitialDuration() {
        service.scheduleAtFixedRate({ counter.incrementAndGet() }, Duration.ofSeconds(1), Duration.ofHours(1))
        assertThat(counter.get(), equalTo(0))
        service.timePasses(Duration.ofMillis(999))
        assertThat(counter.get(), equalTo(0))
        service.timePasses(Duration.ofMillis(1))
        assertThat(counter.get(), equalTo(1))
    }

    @Test
    fun schedulingATaskAtFixedRateWillRunItPeriodically() {
        service.scheduleAtFixedRate({ counter.incrementAndGet() }, Duration.ofSeconds(1), Duration.ofHours(1))
        service.timePasses(Duration.ofSeconds(1))
        assertThat(counter.get(), equalTo(1))
        service.timePasses(Duration.ofMinutes(1))
        assertThat(counter.get(), equalTo(1))
        service.timePasses(Duration.ofMinutes(59))
        assertThat(counter.get(), equalTo(2))
        service.timePasses(Duration.ofHours(1))
        assertThat(counter.get(), equalTo(3))
    }

    @Test
    fun cannotCancelATaskThatHasCompleted() {
        val future = service.schedule<Int>({ counter.incrementAndGet() }, Duration.ofHours(1))
        service.timePasses(Duration.ofHours(1))
        assertThat(counter.get(), equalTo(1))
        val cancelled = future.cancel(true)
        assertThat(cancelled, equalTo(false))
    }

    @Test
    fun cancellingARepeatedTaskBeforeItEverShouldRunNeverRuns() {
        val future = service.scheduleAtFixedRate(
            { counter.incrementAndGet() },
            Duration.ofSeconds(1),
            Duration.ofHours(1)
        )
        future.cancel(true)
        service.timePasses(Duration.ofHours(2))
        assertThat(counter.get(), equalTo(0))
    }

    @Test
    fun schedulingAtFixedRateWillRunItEveryTimePeriod() {
        service.scheduleAtFixedRate({ counter.incrementAndGet() }, Duration.ofSeconds(1), Duration.ofHours(1))
        service.timePasses(Duration.ofSeconds(1))
        assertThat(counter.get(), equalTo(1))
        service.timePasses(Duration.ofHours(5))
        assertThat(counter.get(), equalTo(6))
    }

    @Test
    fun schedulingATaskAtFixedDelayWorksJustTheSameAsFixedRate() {
        service.scheduleWithFixedDelay(
            { counter.incrementAndGet() },
            Duration.ofSeconds(1),
            Duration.ofHours(1)
        )
        service.timePasses(Duration.ofSeconds(1))
        assertThat(counter.get(), equalTo(1))
        service.timePasses(Duration.ofMinutes(1))
        assertThat(counter.get(), equalTo(1))
        service.timePasses(Duration.ofMinutes(59))
        assertThat(counter.get(), equalTo(2))
        service.timePasses(Duration.ofHours(1))
        assertThat(counter.get(), equalTo(3))
    }

    @Test
    fun schedulingATaskDuringTheExecutionOfATaskIsAllowed() {
        val runnable = Runnable { service.schedule({ println("Hello") }, Duration.ofSeconds(2)) }
        service.schedule(runnable, Duration.ofSeconds(1))
        service.timePasses(Duration.ofSeconds(1))
    }

    @Test
    fun schedulingATaskDuringTheExecutionOfATaskWillExecuteIfTimeFallsWithinClockMove() {

        val secondServiceRan = AtomicBoolean(false)

        val runnable = Runnable { service.schedule({ secondServiceRan.set(true) }, Duration.ofSeconds(2)) }
        service.schedule(runnable, Duration.ofSeconds(1))
        service.timePasses(Duration.ofSeconds(100))

        assertThat(
            "expected scheduled task to have run within the time period, but it didn't",
            secondServiceRan.get(),
            equalTo(true)
        )
    }

    @Test
    fun schedulingOneClockTickAfterExecutionOfAnotherTask() {

        val secondServiceRan = AtomicBoolean(false)

        val runnable = Runnable { service.schedule({ secondServiceRan.set(true) }, Duration.ofSeconds(1)) }
        service.schedule(runnable, Duration.ofSeconds(1))
        service.timePasses(Duration.ofSeconds(1))

        assertThat(secondServiceRan.get(), equalTo(false))
    }

    @Test
    fun longTimePeriodsWillInvolveRunningTheServiceMultipleTimes() {

        val atomicLong = AtomicLong(0)

        service.scheduleAtFixedRate(
            { atomicLong.incrementAndGet() },
            Duration.ofMillis(1),
            Duration.ofMinutes(5)
        )
        service.timePasses(Duration.ofHours(1))

        assertThat(atomicLong.get(), equalTo(12L))
    }

    @Test
    fun `can submit simple runnables`() {
        val executionCount = AtomicInteger(0)
        val service = TaskListControllableTaskScheduler()

        val future = service.submit { executionCount.incrementAndGet() }
        assertThat(future.isDone, equalTo(true))
        assertThat(future.isCancelled, equalTo(false))
        assertThat(executionCount.get(), equalTo(1))

        future.get()
        assertThat(future.isDone, equalTo(true))
        assertThat(future.isCancelled, equalTo(false))
        assertThat(executionCount.get(), equalTo(1))
    }

    @Test
    fun `cannot cancel simple runnables`() {
        val executionCount = AtomicInteger(0)
        val service = TaskListControllableTaskScheduler()

        val future = service.submit { executionCount.incrementAndGet() }
        assertThat(future.isDone, equalTo(true))
        assertThat(future.isCancelled, equalTo(false))
        assertThat(executionCount.get(), equalTo(1))

        future.cancel(true)
        assertThat(future.isDone, equalTo(true))
        assertThat(future.isCancelled, equalTo(false))
    }

    @Test
    fun `can submit simple callables`() {
        val executionCount = AtomicInteger(0)
        val service = TaskListControllableTaskScheduler()

        val future: Future<Int> = service.submit(Callable { return@Callable executionCount.incrementAndGet() })
        assertThat(future.isDone, equalTo(true))
        assertThat(future.isCancelled, equalTo(false))
        assertThat(executionCount.get(), equalTo(1))

        assertThat(future.get(), equalTo(1))
        assertThat(future.isDone, equalTo(true))
        assertThat(future.isCancelled, equalTo(false))
        assertThat(executionCount.get(), equalTo(1))
    }

    @Test
    fun `cannot cancel simple callables`() {
        val executionCount = AtomicInteger(0)
        val service = TaskListControllableTaskScheduler()

        val future = service.submit(Callable { return@Callable executionCount.incrementAndGet() })
        assertThat(future.isDone, equalTo(true))
        assertThat(future.isCancelled, equalTo(false))
        assertThat(executionCount.get(), equalTo(1))

        future.cancel(true)
        assertThat(future.isDone, equalTo(true))
        assertThat(future.isCancelled, equalTo(false))
    }
}
