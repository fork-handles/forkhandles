package dev.forkhandles.time

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertSame
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Assertions.fail
import org.junit.jupiter.api.Test
import java.lang.IllegalArgumentException
import java.time.Duration
import java.time.Instant
import java.util.concurrent.Callable
import java.util.concurrent.CancellationException
import java.util.concurrent.ExecutionException
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.MILLISECONDS
import java.util.concurrent.TimeUnit.SECONDS
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong

class DeterministicSchedulerTests {
    private val scheduler = DeterministicScheduler()

    private val invoked: MutableList<Any> = ArrayList()

    private val commandA = trackedRunnable("commandA")
    private val commandB = trackedRunnable("commandB")
    private val commandC = trackedRunnable("commandC")
    private val commandD = trackedRunnable("commandD")

    @Test
    fun runsPendingCommandsUntilIdle() {
        scheduler.execute(commandA)
        scheduler.execute(commandB)

        assertFalse(scheduler.isIdle())

        scheduler.runUntilIdle()

        assertTrue(scheduler.isIdle())

        assertEquals(listOf(commandA, commandB), invoked)
    }

    @Test
    fun canRunCommandsSpawnedByExecutedCommandsUntilIdle() {
        scheduler.execute { scheduler.execute(commandA) }
        scheduler.execute { scheduler.execute(commandB) }

        scheduler.runUntilIdle()

        assertTrue(scheduler.isIdle())

        assertEquals(listOf(commandA, commandB), invoked)
    }

    @Test
    fun canScheduleCommandAndReturnFuture() {
        val future = scheduler.submit(commandA)

        assertFalse(future.isDone, "future should not be done before running the task")

        scheduler.runUntilIdle()

        assertEquals(listOf(commandA), invoked)
        assertTrue(future.isDone, "future should be done after running the task")
        assertNull(future.get(), "result of future should be null")
    }

    @Test
    fun canScheduleCommandAndResultAndReturnFuture() {
        val future = scheduler.submit(commandA, "result1")

        scheduler.runUntilIdle()

        assertEquals(listOf(commandA), invoked)

        assertEquals("result1", future.get())
    }

    @Test
    fun canScheduleCallableAndReturnFuture() {
        val future = scheduler.submit(trackedCallable("result2"))

        scheduler.runUntilIdle()

        assertEquals("result2", future.get())
    }

    @Test
    fun scheduledCallablesCanReturnNull() {
        val future = scheduler.submit(trackedCallable<String?>(null))

        scheduler.runUntilIdle()

        assertNull(future.get())
    }

    @Test
    fun simpleGetDelay() {
        val task = scheduler.schedule(commandA, 10, SECONDS)

        scheduler.tick(1, SECONDS)

        assertEquals(9, task.getDelay(SECONDS))
    }

    @Test
    fun getDelayWithManyScheduledTasks() {
        val future1 = scheduler.schedule(commandA, 10, SECONDS)
        val future2 = scheduler.schedule(commandA, 20, SECONDS)
        val future3 = scheduler.schedule(commandA, 15, SECONDS)

        scheduler.tick(5, SECONDS)

        assertEquals(5, future1.getDelay(SECONDS))
        assertEquals(15, future2.getDelay(SECONDS))
        assertEquals(10, future3.getDelay(SECONDS))
    }

    @Test
    fun getDelayOnPassedTasks() {
        val exception = IllegalStateException()

        val future = scheduler.schedule({ throw exception }, 1, MILLISECONDS)

        scheduler.tick(2, MILLISECONDS)

        assertEquals(-1, future.getDelay(MILLISECONDS))
    }

    class ExampleException : Exception()

    @Test
    fun exceptionThrownByScheduledCallablesIsThrownFromFuture() {
        val exception = ExampleException()

        val future = scheduler.submit(Callable<Any> { throw exception })

        scheduler.runUntilIdle()

        try {
            future.get()
            fail<Any>("should have thrown ExecutionException")
        } catch (expected: ExecutionException) {
            assertSame(exception, expected.cause)
        }
    }

    @Test
    fun canScheduleCommandsToBeExecutedAfterADelay() {
        scheduler.schedule(commandA, 10, SECONDS)

        scheduler.tick(9, SECONDS)
        scheduler.tick(1, SECONDS)

        assertEquals(listOf(commandA), invoked)
    }

    @Test
    fun tickingTimeForwardRunsAllCommandsScheduledDuringThatTimePeriod() {
        scheduler.schedule(commandA, 1, MILLISECONDS)
        scheduler.schedule(commandB, 2, MILLISECONDS)

        scheduler.tick(3, MILLISECONDS)

        assertEquals(listOf(commandA, commandB), invoked)
    }

    @Test
    fun tickingTimeForwardRunsCommandsExecutedByScheduledCommands() {
        scheduler.schedule(
            { scheduler.execute { scheduler.execute(commandC) } },
            1, MILLISECONDS
        )

        scheduler.schedule(commandD, 2, MILLISECONDS)

        scheduler.tick(3, MILLISECONDS)

        assertEquals(listOf(commandC, commandD), invoked)
    }

    @Test
    fun commandsCanSeeTheProgressionOfSimulatedTimeFromTheSchedulersTimeSource() {
        val scheduler = DeterministicScheduler(Instant.ofEpochSecond(1))

        val now: TimeSource = scheduler::currentTime

        scheduler.schedule({
            invoked.add("task1")
            assertEquals(Instant.ofEpochSecond(2), now())
        }, 1, SECONDS)
        scheduler.schedule({
            invoked.add("task2")
            assertEquals(Instant.ofEpochSecond(2), now())
        }, 1, SECONDS)
        scheduler.schedule({
            invoked.add("task3")
            assertEquals(Instant.ofEpochSecond(3), now())
        }, 2, SECONDS)

        scheduler.tick(5, SECONDS)

        assertEquals(listOf("task1", "task2", "task3"), invoked)

        assertEquals(Instant.ofEpochSecond(6), now())
    }

    @Test
    fun canExecuteCommandsThatRepeatWithFixedDelay() {
        scheduler.scheduleWithFixedDelay(commandA, 2L, 3L, SECONDS)

        scheduler.tick(8L, SECONDS)

        assertEquals(listOf(commandA, commandA, commandA), invoked)
    }

    @Test
    fun canExecuteCommandsThatRepeatAtFixedRateButAssumesThatCommandsTakeNoTimeToExecute() {
        scheduler.scheduleAtFixedRate(commandA, 2L, 3L, SECONDS)

        scheduler.tick(8L, SECONDS)

        assertEquals(listOf(commandA, commandA, commandA), invoked)
    }

    @Test
    fun canCancelScheduledCommands() {
        val dontCare = true

        val future = scheduler.schedule(commandA, 1, SECONDS)

        assertFalse(future.isCancelled)

        future.cancel(dontCare)

        assertTrue(future.isCancelled)

        scheduler.tick(2, SECONDS)

        assertTrue(invoked.isEmpty())
    }

    @Test
    fun testCancellingARunningCommandStopsItFromRunningAgain() {
        var future: ScheduledFuture<*>? = null
        var runCount = 0

        future = scheduler.scheduleAtFixedRate({
            runCount++
            future?.cancel(true)
        }, 1, 1, SECONDS)

        scheduler.tick(2, SECONDS)

        assertEquals(1, runCount, "cancelling runnable run count")
    }

    @Test
    fun testCanScheduleCallablesAndGetTheirResultAfterTheyHaveBeenExecuted() {
        val future = scheduler.schedule(
            trackedCallable("A"), 1, SECONDS
        )
        assertFalse(future.isDone, "is not done")
        scheduler.tick(1, SECONDS)
        assertTrue(future.isDone, "is done")
        assertEquals("A", future.get())
        assertEquals("A", future[TIMEOUT_IGNORED.toLong(), SECONDS])
    }

    @Test
    fun testCannotBlockWaitingForFutureResultOfScheduledCallable() {
        val task = trackedCallable("result")
        val future = scheduler.schedule(task, 1, SECONDS)
        assertThrows(UnsupportedOperationException::class.java) { future.get() }
        assertThrows(UnsupportedOperationException::class.java) { future[TIMEOUT_IGNORED.toLong(), SECONDS] }
    }


    @Test
    fun testCancellingAFutureThatIsNotYetExecuted() {
        val task = trackedCallable("result")
        val future = scheduler.schedule(task, 1, SECONDS)
        val wasAbleToCancel = future.cancel(true)
        assertEquals(true, wasAbleToCancel, "was able to cancel")
        assertEquals(true, future.isCancelled, "isCancelled")
        assertEquals(true, future.isDone, "isDone")
        assertThrows(CancellationException::class.java) { future.get() }
    }

    @Test
    fun testCancellingAFutureThatIsAlreadyExecuted() {
        val task = trackedCallable("result")
        val future = scheduler.schedule(task, 1, SECONDS)
        assertEquals(false, future.isDone, "isDone")

        scheduler.tick(Duration.ofSeconds(1))

        assertEquals(true, future.isDone, "isDone")

        val wasAbleToCancel = future.cancel(true)

        assertEquals(false, wasAbleToCancel, "was able to cancel")
        assertEquals(false, future.isCancelled, "isCancelled")
        future.get()
    }

    @Test
    fun rejectScheduleIfOutOfBounds() {
        val task = trackedRunnable("thing")
        assertThrows(IllegalArgumentException::class.java) { scheduler.scheduleWithFixedDelay(task, 1, -1, MILLISECONDS) }
        assertThrows(IllegalArgumentException::class.java) { scheduler.scheduleWithFixedDelay(task, 1, 0, MILLISECONDS) }
        assertThrows(IllegalArgumentException::class.java) { scheduler.scheduleAtFixedRate(task, 1, -1, MILLISECONDS) }
        assertThrows(IllegalArgumentException::class.java) { scheduler.scheduleAtFixedRate(task, 1, 0, MILLISECONDS) }
    }

    @Test
    fun isNotShutdownUntilItIs() {
        assertEquals(false, scheduler.isShutdown)
        scheduler.shutdown()
        assertEquals(true, scheduler.isShutdown)
    }

    @Test
    fun isNotShutdownUntilItIsNow() {
        assertEquals(false, scheduler.isShutdown)
        scheduler.shutdownNow()
        assertEquals(true, scheduler.isShutdown)
    }

    @Test
    fun isTerminatedAsSoonAsItIsShutdown() {
        assertEquals(false, scheduler.isTerminated)
        scheduler.shutdown()
        assertEquals(true, scheduler.isTerminated)
    }

    @Test
    fun cannotWaitForTerminationUntilShutdown() {
        // this is a small bodge to the differences between sync and async operation - but here we assume the same
        // thread calls shutdown & await termination, like if shutting down a service under test
        assertThrows(UnsupportedSynchronousOperationException::class.java) { scheduler.awaitTermination(1, SECONDS) }
    }

    @Test
    fun waitForTerminationWhenShutdown() {
        scheduler.shutdown()
        assertTrue(scheduler.awaitTermination(1, SECONDS))
    }

    @Test
    fun tasksSubmittedAfterShutdownAreIgnored() {
        val counter = AtomicInteger()
        scheduler.shutdown()
        scheduler.schedule({ counter.incrementAndGet() }, 1, SECONDS)
        scheduler.tick(Duration.ofSeconds(2))
        assertEquals(0, counter.get())
    }

    @Test
    fun tasksAreExecutedUntilShutdown() {
        val counter = AtomicInteger()
        scheduler.schedule({ counter.incrementAndGet(); scheduler.shutdown() }, 1, SECONDS)
        scheduler.tick(Duration.ofSeconds(2))
        assertEquals(1, counter.get())
    }

    @Test
    fun longTimePeriodsWillInvolveRunningTheServiceMultipleTimes() {

        val counter = AtomicLong(0)

        scheduler.scheduleAtFixedRate({ counter.incrementAndGet() }, 1, 5, TimeUnit.MINUTES)
        scheduler.tick(Duration.ofHours(1))

        assertEquals(12, counter.get())
    }

    @Test
    fun periodicTaskThrowingExceptionSuppressesSubsequentExecutions() {
        val counter = AtomicLong(0)
        scheduler.scheduleAtFixedRate({ counter.incrementAndGet(); throw NullPointerException() }, 0, 1, SECONDS)
        scheduler.tick(Duration.ofSeconds(3))
        assertEquals(1, counter.get())
    }

    @Test
    fun bugfixed_schedulingMultipleTasksWithSameDelay_ShouldNoLongerCrash() {
        scheduler.scheduleWithFixedDelay(commandA, 2L, 3L, SECONDS)
        scheduler.scheduleWithFixedDelay(commandB, 2L, 3L, SECONDS)

        scheduler.tick(8L, SECONDS)

        assertEquals(listOf(commandA, commandB, commandA, commandB, commandA, commandB), invoked)
    }

    private fun trackedRunnable(name: String): Runnable {
        return object : Runnable {
            override fun run() {
                invoked.add(this)
            }

            override fun toString(): String {
                return name
            }
        }
    }

    private fun <T> trackedCallable(result: T): Callable<T> {
        return object : Callable<T> {
            @Throws(Exception::class)
            override fun call(): T {
                invoked.add(this)
                return result
            }

            override fun toString(): String {
                return result.toString()
            }
        }
    }

    companion object {
        const val TIMEOUT_IGNORED = 1000
    }
}
