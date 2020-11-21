package dev.forkhandles.values

typealias Validation<T> = T.() -> Boolean

val Int.maxLength: Validation<String> get() = { length <= this@maxLength }
val Int.minLength: Validation<String> get() = { length >= this@minLength }
val Int.exactLength: Validation<String> get() = { length == this@exactLength }
val IntRange.length: Validation<String> get() = { this@length.contains(length) }

val Int.maxValue: Validation<Int> get() = { this <= this@maxValue }
val Int.minValue: Validation<Int> get() = { this >= this@minValue }
val IntRange.value: Validation<Number> get() = { this@value.contains(this) }

val Long.maxValue: Validation<Long> get() = { this <= this@maxValue }
val Long.minValue: Validation<Long> get() = { this >= this@minValue }
val LongRange.value: Validation<Number> get() = { this@value.contains(this) }

val Number.exactValue: Validation<Number> get() = { this == this@exactValue }

fun <T> Validation<T>.and(that: Validation<T>): Validation<T> = { this@and(this) && that(this) }
fun <T> Validation<T>.or(that: Validation<T>): Validation<T> = { this@or(this) || that(this) }
operator fun <T> Validation<T>.not(): Validation<T> = { !this@not(this) }
