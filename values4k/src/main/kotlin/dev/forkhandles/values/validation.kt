package dev.forkhandles.values

/**
 * Allows validation on values coming in to ensure only legal values construction.
 */
typealias Validation<T> = (T) -> Boolean

val Int.maxLength: Validation<String> get() = { it.length <= this@maxLength }
val Int.minLength: Validation<String> get() = { it.length >= this@minLength }
val Int.exactLength: Validation<String> get() = { it.length == this@exactLength }
val IntRange.length: Validation<String> get() = { this@length.contains(it.length) }
val String.regex: Validation<String> get() = toRegex().let { v -> { v.matches(it) } }

val <T : Comparable<T>> T.maxValue: Validation<T> get() = { it <= this@maxValue }
val <T : Comparable<T>> T.minValue: Validation<T> get() = { it >= this@minValue }
val <T : Comparable<T>> ClosedRange<T>.between: Validation<T> get() = { this@between.contains(it) }

val Number.exactValue: Validation<Number> get() = { it == this@exactValue }

fun <T> Validation<T>.and(that: Validation<T>): Validation<T> = { this@and(it) && that(it) }
fun <T> Validation<T>.or(that: Validation<T>): Validation<T> = { this@or(it) || that(it) }
operator fun <T> Validation<T>.not(): Validation<T> = { !this@not(it) }

fun <T> Validation<T>.check(value: T) {
    require(this(value))
    { "Validation failed for: ${this@check.javaClass.simpleName}(${value.toString().takeIf { it.isNotBlank() } ?: "\"\""})" }
}
