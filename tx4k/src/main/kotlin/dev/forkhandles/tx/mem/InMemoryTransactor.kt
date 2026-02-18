@file:OptIn(ExperimentalAtomicApi::class)
package dev.forkhandles.tx.mem

import dev.forkhandles.tx.Transactor
import kotlin.concurrent.atomics.AtomicReference
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.Unit as noop

class InMemoryTransaction<State>(val initialState: State) {
    var state: State = initialState
}

class InMemoryTransactor<State, out API>(
    initialState: State,
    private val createRepository: (InMemoryTransaction<State>)->API
) : Transactor<InMemoryTransaction<State>, API>() {
    val state = AtomicReference(initialState)
    
    override fun createResource() = InMemoryTransaction(state.load())
    override fun configureResource(resource: InMemoryTransaction<State>) = noop
    override fun destroyResource(resource: InMemoryTransaction<State>) = noop
    
    override fun createApi(resource: InMemoryTransaction<State>) =
        createRepository(resource)
    
    override fun startTransaction(resource: InMemoryTransaction<State>) = noop
    override fun rollbackTransaction(resource: InMemoryTransaction<State>) = noop
    override fun commitTransaction(resource: InMemoryTransaction<State>) {
        if (!state.compareAndSet(expectedValue = resource.initialState, newValue = resource.state)) {
            throw RetryException()
        }
    }
    
    override fun canRetry(e: Exception) = e is RetryException
    
    internal class RetryException : Exception()
}
