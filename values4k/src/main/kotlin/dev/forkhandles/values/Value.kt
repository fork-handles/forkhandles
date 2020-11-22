package dev.forkhandles.values

import dev.forkhandles.values.Maskers.public

/**
 * Base value type which enables type-safe primitives, along with Validation and Masking.
 */
abstract class Value<T : Any> @JvmOverloads constructor(
    val value: T,
    validation: Validation<T>? = null,
    private val masking: Masking<T> = public
) {
    init {
        validation?.check(value)
    }

    override fun toString() = masking(value)

    override fun hashCode() = value.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Value<*>

        if (value != other.value) return false

        return true
    }
}
