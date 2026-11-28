@file:Suppress("DEPRECATION")

package dev.forkhandles.values

/**
 * Allows validation on values coming in to ensure only legal values construction.
 */
@Deprecated("Use Validator<T> instead")
typealias Validation<T> = (T) -> Boolean

val Int.maxLength: Validator<String> get() = size(max = this)

val Int.minLength: Validator<String> get() = size(min = this)

val Int.exactLength: Validator<String> get() = size(min = this, max = this)

val IntRange.length: Validator<String> get() = size(min = first, max = last)

val String.regex: Validator<String> get() = pattern(this)

val <T : Comparable<T>> T.maxValue: Validator<T> get() = max(this)

val <T : Comparable<T>> T.minValue: Validator<T> get() = min(this)

val <T : Comparable<T>> ClosedRange<T>.between: Validator<T> get() = min(start) and max(endInclusive)

val Number.exactValue: Validator<Number> get() {
    val expected = this
    return { value ->
        if (value != expected) ConstraintProblem(Constraint("Exact"), Message("must be equal to $expected"), value.toString()) else null
    }
}

@Deprecated("Use Validator.and instead")
fun <T> Validation<T>.and(that: Validation<T>): Validation<T> = { this@and(it) && that(it) }

@Deprecated("Use Validator.or instead")
fun <T> Validation<T>.or(that: Validation<T>): Validation<T> = { this@or(it) || that(it) }

@Deprecated("No direct Validator equivalent yet")
operator fun <T> Validation<T>.not(): Validation<T> = { !this@not(it) }

@Deprecated("Use Validator.check instead")
fun <T> Validation<T>.check(value: T) {
    require(this(value))
    { "Validation failed for: ${this@check.javaClass.simpleName}(${value.toString().takeIf { it.isNotBlank() } ?: "\"\""})" }
}
