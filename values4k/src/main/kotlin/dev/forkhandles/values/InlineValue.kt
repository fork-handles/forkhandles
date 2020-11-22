package dev.forkhandles.values

/**
 * Base value type for inline classes which enables type-safe primitives, along with Validation.
 */
abstract class InlineValue<V, T>(private val fn: (T) -> V, private val validation: Validation<T>? = null) {
    fun of(value: T): V {
        validation?.check(value)
        return fn(value)
    }
}
