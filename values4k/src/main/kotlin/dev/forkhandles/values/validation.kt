package dev.forkhandles.values

/**
 * Allows validation on values coming in to ensure only legal values construction.
 */
typealias Validation<T> = (T) -> Boolean

val Int.maxLength: Validation<String> get() = { it.length <= this@maxLength }
val Int.minLength: Validation<String> get() = { it.length >= this@minLength }
val Int.exactLength: Validation<String> get() = { it.length == this@exactLength }
val IntRange.length: Validation<String> get() = { this@length.contains(it.length) }

val Int.maxValue: Validation<Int> get() = { it <= this@maxValue }
val Int.minValue: Validation<Int> get() = { it >= this@minValue }
val IntRange.value: Validation<Number> get() = { this@value.contains(it) }

val Long.maxValue: Validation<Long> get() = { it <= this@maxValue }
val Long.minValue: Validation<Long> get() = { it >= this@minValue }
val LongRange.value: Validation<Number> get() = { this@value.contains(it) }

val Number.exactValue: Validation<Number> get() = { it == this@exactValue }

/**
 * Be careful when you are using this - Regex compilation can be expensive, so it's worth
 * externalising the instances of these Validations. eg:
 *
 * val pattern = "\\d{6}".regex // make only one!
 * class SortCode(value: String) : Value<String>(value, pattern)
 */
val String.regex: Validation<String> get() = toRegex().let { v -> { v.matches(it) } }

fun <T> Validation<T>.and(that: Validation<T>): Validation<T> = { this@and(it) && that(it) }
fun <T> Validation<T>.or(that: Validation<T>): Validation<T> = { this@or(it) || that(it) }
operator fun <T> Validation<T>.not(): Validation<T> = { !this@not(it) }

fun <T> Validation<T>.check(value: T) {
    require(this(value))
    { "Validation failed for: ${this@check.javaClass.simpleName}(${value.toString().takeIf { it.isNotBlank() } ?: "\"\""})" }
}
