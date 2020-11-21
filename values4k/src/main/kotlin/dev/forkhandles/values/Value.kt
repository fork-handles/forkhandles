package dev.forkhandles.values

/**
 * Base value type which enables type-safe primitives.
 */
abstract class Value<T> private constructor(validation: Validation<T>?, val value: T) {
    constructor(value: T) : this(null, value)
    constructor(value: T, validation: Validation<T>?) : this(validation, value)

    init {
        validation?.let {
            require(it(value))
            { "Validation failed for: ${javaClass.simpleName}(${value.toString().takeIf { it.isNotBlank() } ?: "\"\""})" }
        }
    }
    override fun toString() = value.toString()

    override fun hashCode() = value?.hashCode() ?: 0

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Value<*>

        if (value != other.value) return false

        return true
    }
}
