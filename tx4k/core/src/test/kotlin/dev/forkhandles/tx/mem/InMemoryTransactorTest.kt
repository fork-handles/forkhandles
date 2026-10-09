@file:OptIn(kotlin.concurrent.atomics.ExperimentalAtomicApi::class)

package dev.forkhandles.tx.mem

import com.natpryce.hamkrest.assertion.assertThat
import dev.forkhandles.tx.Counter
import dev.forkhandles.tx.TransactorContract
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test


class InMemoryTransactorTest : TransactorContract() {
    class InMemoryCounter(val tx: InMemoryTransaction<Int>) : Counter {
        override fun incrementBy(n: Int) {
            tx.state += n
        }
        
        override fun count() = tx.state
    }
    
    override val transactor = InMemoryTransactor(0, ::InMemoryCounter)

    @Test
    fun `retries a conflicting transaction from the newly committed state`() {
        val seen = mutableListOf<Int>()

        transactor.perform { counter ->
            seen += counter.count()
            if (seen.size == 1) transactor.state.store(100)
            counter.incrementBy(1)
        }

        assertEquals(listOf(0, 100), seen)
        assertEquals(101, transactor.state.load())
    }
}
