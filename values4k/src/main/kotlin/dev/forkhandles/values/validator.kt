@file:Suppress("DEPRECATION")

package dev.forkhandles.values

import kotlin.reflect.KProperty1

typealias Validator<T> = (T) -> Problem?

enum class FailureMode { FailFast, CollectAll }

fun <T> Validation<T>.toValidator(constraint: Constraint, message: Message): Validator<T> = { value ->
    if (this(value)) null else ConstraintProblem(constraint, message, value.toString())
}

fun <T> Validation<T>.toValidator(): Validator<T> = toValidator(Constraint.Invalid, Message("validation failed"))

infix fun <T> Validator<T>.and(other: Validator<T>): Validator<T> = and(other, FailureMode.FailFast)

fun <T> Validator<T>.and(other: Validator<T>, mode: FailureMode): Validator<T> = { value ->
    when (mode) {
        FailureMode.FailFast -> this(value) ?: other(value)
        FailureMode.CollectAll -> allOf(this, other)(value)
    }
}

infix fun <T> Validator<T>.or(other: Validator<T>): Validator<T> = { value ->
    val left = this(value)
    val right = other(value)
    when {
        left == null || right == null -> null
        else -> Problems(listOf(left, right))
    }
}

fun <T> allOf(vararg validators: Validator<T>): Validator<T> = { value ->
    validators.mapNotNull { it(value) }.flatMap { it.causes() }.let { causes ->
        if (causes.isEmpty()) null else Problems(causes)
    }
}

fun <T> Validator<T>.check(value: T) {
    this(value)?.let { throw ValidationException(it.causes()) }
}

infix fun <T, V> KProperty1<T, V>.validated(validator: Validator<V>): Validator<T> = { t ->
    validator(this.get(t))
}
