@file:OptIn(kotlin.concurrent.atomics.ExperimentalAtomicApi::class)

package dev.forkhandles.tx.mem

import dev.forkhandles.tx.Counter
import dev.forkhandles.tx.TransactorContract


class InMemoryTransactorTest : TransactorContract() {
    class InMemoryCounter(val tx: InMemoryTransaction<Int>) : Counter {
        override fun incrementBy(n: Int) {
            tx.state += n
        }
        
        override fun count() = tx.state
    }
    
    override val transactor = InMemoryTransactor(0, ::InMemoryCounter)
}
