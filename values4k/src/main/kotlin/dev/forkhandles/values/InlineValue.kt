package dev.forkhandles.values

/**
 * Base value type for inline classes which enables type-safe primitives, along with Validation.
 */

interface AValue<T>

abstract class InlineValue<T, V>(private val fn: (T) -> V, private val validation: Validation<T>? = null) {
    fun of(value: T): V {
        validation?.check(value)
        return fn(value)
    }
}
