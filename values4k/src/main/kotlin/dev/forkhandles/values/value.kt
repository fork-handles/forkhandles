package dev.forkhandles.values

import dev.forkhandles.values.Maskers.public

/**
 * Base value interface which enables type-safe primitives, along with Validation.
 */
interface Value<T : Any> {
    val value: T
}

abstract class AbstractValue<T : Any> @JvmOverloads constructor(
    override val value: T,
    private val masking: Masking<T> = public
) : Value<T> {
    override fun toString() = masking(value)

    override fun hashCode() = value.hashCode()

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AbstractValue<*>

        if (value != other.value) return false

        return true
    }
}

/**
 * Comparable mix-in.
 */
interface Comparable4k<T, V> : Comparable<V>, Value<T> where T : Any, T : Comparable<T>, V : Value<T> {
    override fun compareTo(other: V) = value.compareTo(other.value)
}
