package dev.forkhandles.values

@Deprecated("renamed", ReplaceWith("between"))
val <T : Comparable<T>> ClosedRange<T>.value: Validation<T> get() = between
