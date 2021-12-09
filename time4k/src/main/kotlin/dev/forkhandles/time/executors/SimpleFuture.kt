package dev.forkhandles.time.executors

import java.util.concurrent.Callable
import java.util.concurrent.CancellationException
import java.util.concurrent.Future
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference

class SimpleFuture<V>(private val task: Callable<V>) : Future<V> {
    private val done: AtomicBoolean = AtomicBoolean(false)
    private val cancelled: AtomicBoolean = AtomicBoolean(false)
    private val result: AtomicReference<V> = AtomicReference()

    override fun isDone(): Boolean = done.get()

    override fun get(): V {
        if (cancelled.get()) throw CancellationException()
        if (done.compareAndSet(false, true)) result.set(task.call())
        return result.get()
    }

    override fun get(timeout: Long, unit: TimeUnit?): V = get()

    override fun cancel(mayInterruptIfRunning: Boolean): Boolean {
        if (done.get()) return false
        cancelled.set(true)
        done.set(true)
        return true
    }

    override fun isCancelled(): Boolean = cancelled.get()
}
