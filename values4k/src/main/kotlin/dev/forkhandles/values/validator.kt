package dev.forkhandles.values

import kotlin.reflect.KProperty1

typealias Validator<T> = (T) -> Problem?

fun <T> Validation<T>.toValidator(constraint: Constraint, message: Message): Validator<T> = { value ->
    if (this(value)) null else ConstraintProblem(constraint, message, value.toString())
}

infix fun <T> Validator<T>.and(other: Validator<T>): Validator<T> = { value ->
    this(value) ?: other(value)
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

infix fun <T, V> KProperty1<T, V>.validated(validator: Validator<V>): Validator<T> = { t ->
    validator(this.get(t))
}
