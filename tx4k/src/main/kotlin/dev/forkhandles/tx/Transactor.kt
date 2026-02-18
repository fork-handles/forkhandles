package dev.forkhandles.tx

import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

abstract class Transactor<Resource,out API> {
    abstract fun createResource(): Resource
    abstract fun configureResource(resource: Resource)
    abstract fun destroyResource(resource: Resource)
    
    abstract fun createApi(resource: Resource): API
    
    abstract fun startTransaction(resource: Resource)
    abstract fun commitTransaction(resource: Resource)
    abstract fun rollbackTransaction(resource: Resource)
    
    abstract fun canRetry(e: Exception): Boolean
    
    // Inline so that the `work` lambda can do an early return
    @OptIn(ExperimentalContracts::class)
    inline fun <Result> perform(work: (API) -> Result): Result {
        contract {
            callsInPlace(work, InvocationKind.AT_LEAST_ONCE)
        }
        
        val resource = createResource()
        try {
            configureResource(resource)
            val api = createApi(resource)
            
            var attempts = 0
            while (true) try {
                startTransaction(resource)
                val res = work(api)
                commitTransaction(resource)
                return res
            }
            catch (e: Exception) {
                rollbackTransaction(resource)
                if (canRetry(e)) {
                    attempts++
                    if (attempts >= 4) {
                        throw e
                    } else {
                        Thread.sleep(10) // TODO: exponential backoff and jitter
                    }
                } else {
                    throw e
                }
            }
        } finally {
            destroyResource(resource)
        }
    }
}
