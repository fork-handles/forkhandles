package dev.forkhandles.values

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.resultFrom

/**
 * Base value type for inline classes which enables type-safe primitives, along with Validation.
 */
abstract class ValueFactory<V, T>(val fn: (T) -> V, val validation: Validation<T>? = null) {
    internal fun validate(value: T): V {
        validation?.check(value)
        return fn(value)
    }

    fun of(value: T): V = validate(value)
}

/**
 * Return a Object/null based on validation
 */
fun <V, T> ValueFactory<V, T>.ofNullable(value: T): V? = try {
    validate(value)
} catch (e: IllegalArgumentException) {
    null
}

/**
 * Return a Result4k Success/Failure based on validation
 */
fun <V, T> ValueFactory<V, T>.ofResult4k(value: T): Result<V, Exception> = resultFrom { validate(value) }
