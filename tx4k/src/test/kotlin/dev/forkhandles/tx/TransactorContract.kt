@file:OptIn(kotlin.concurrent.atomics.ExperimentalAtomicApi::class)

package dev.forkhandles.tx

import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit.SECONDS
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.incrementAndFetch
import kotlin.test.Test
import kotlin.test.assertEquals

abstract class TransactorContract {
    abstract val transactor: Transactional<Counter>
    
    @Test
    fun `with one thread`() {
        testWith(Executors.newSingleThreadExecutor(), 180)
    }
    
    @Test
    fun `with multiple threads`() {
        testWith(Executors.newFixedThreadPool(5), 400)
    }
    
    @Test
    fun `with multiple virtual threads`() {
        testWith(Executors.newFixedThreadPool(5, Thread.ofVirtual().factory()), 600)
    }
    
    private fun testWith(executorService: ExecutorService, intendedWriteCount: Int) {
        val failureCount = AtomicInt(0)
        
        executorService.use {
            useCounter(it, intendedWriteCount, failureCount)
        }
        executorService.awaitTermination(10, SECONDS)
        
        transactor.perform { counter ->
            val actualWriteCount = counter.count()
            
            assertEquals(intendedWriteCount, actualWriteCount + failureCount.load())
        }
    }
    
    private fun useCounter(executor: Executor, count: Int, failureCount: AtomicInt) {
        repeat(count * 2) { n ->
            executor.execute {
                try {
                    transactor.perform { counter ->
                        when (n % 2) {
                            1 -> throw ForceARollback()
                            else -> counter.incrementBy(1)
                        }
                    }
                } catch (_: SerialisabilityFailure) {
                    failureCount.incrementAndFetch()
                } catch (_: ForceARollback) {
                    // Expected, but catch here to prevent the executor writing a lot of noise to stderr
                }
            }
        }
    }
    
    private class ForceARollback() : Exception()
}
