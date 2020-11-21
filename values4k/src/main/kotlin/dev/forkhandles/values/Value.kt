package dev.forkhandles.values

abstract class Value<T>(val value: T) {
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

abstract class ValidatedValue<T>(value: T, validator: (T) -> Boolean) : Value<T>(value) {
    init {
        require(validator(value))
    }
}
