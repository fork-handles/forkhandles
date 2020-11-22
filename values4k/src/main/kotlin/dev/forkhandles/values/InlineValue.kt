package dev.forkhandles.values

/**
 * Base value type for inline classes which enables type-safe primitives, along with Validation.
 */
abstract class InlineValue<T>(private val validation: Validation<T>? = null) {
    fun of(value: T) {
        validation?.also {
            require(it(value))
            { "Validation failed for: ${javaClass.simpleName}(${value.toString().takeIf { it.isNotBlank() } ?: "\"\""})" }
        }
    }
}
