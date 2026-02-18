@file:OptIn(ExperimentalAtomicApi::class)

package dev.forkhandles.tx.mem

import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.concurrent.atomics.AtomicInt
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.incrementAndFetch
import kotlin.test.Test
import kotlin.test.assertEquals


class InMemoryTransactorTest {
    class InMemoryCounter(val tx: InMemoryTransaction<Int>) {
        fun incrementBy(n: Int) {
            tx.state += n
        }
        
        fun count() = tx.state
    }
    
    val transactor = InMemoryTransactor(0, ::InMemoryCounter)
    
    @Test
    fun `with one thread`() {
        testWith(Executors.newSingleThreadExecutor(), 500)
    }
    
    @Test
    fun `with multiple threads`() {
        testWith(Executors.newFixedThreadPool(3), 600)
    }
    
    @Test
    fun `with multiple virtual threads`() {
        testWith(Executors.newVirtualThreadPerTaskExecutor(), 480)
    }
    
    private fun testWith(executorService: ExecutorService, count: Int) {
        val failureCount = AtomicInt(0)
        
        executorService.use {
            useCounter(it, count, failureCount)
        }
        
        transactor.perform { counter ->
            assertEquals(count, counter.count() + failureCount.load())
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
                } catch (_: InMemoryTransactor.RetryException) {
                    failureCount.incrementAndFetch()
                } catch (_: ForceARollback) {
                    // Expected – catch here to prevent the executor outputing a lot of noise to stderr
                }
            }
        }
    }
    
    private class ForceARollback() : Exception()
}
