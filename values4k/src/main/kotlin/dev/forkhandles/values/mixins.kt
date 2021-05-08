package dev.forkhandles.values

interface Comparable4k<T, V> : Comparable<V>, Value<T> where T : Any, T : Comparable<T>, V : Value<T> {
    override fun compareTo(other: V) = value.compareTo(other.value)
}
