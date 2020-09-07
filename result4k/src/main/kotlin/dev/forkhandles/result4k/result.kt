@file:Suppress("NonAsciiCharacters")

package dev.forkhandles.result4k

/**
 * A result of a computation that can succeed or fail.
 */
sealed class Result<out T, out E>

data class Success<out T>(val value: T) : Result<T, Nothing>()
data class Failure<out E>(val reason: E) : Result<Nothing, E>()

/**
 * Call a function and wrap the result in a Result, catching any Exception and returning it as Err value.
 */
inline fun <T> resultFrom(block: () -> T): Result<T, Exception> =
    try {
        Success(block())
    } catch (x: Exception) {
        Failure(x)
    }

/**
 * Map a function over the _value_ of a successful Result.
 */
inline fun <T, Tʹ, E> Result<T, E>.map(f: (T) -> Tʹ): Result<Tʹ, E> =
    flatMap { value -> Success(f(value)) }

/**
 * Flat-map a function over the _value_ of a successful Result.
 */
inline fun <T, Tʹ, E> Result<T, E>.flatMap(f: (T) -> Result<Tʹ, E>): Result<Tʹ, E> =
    when (this) {
        is Success<T> -> f(value)
        is Failure<E> -> this
    }

/**
 * Flat-map a function over the _reason_ of a unsuccessful Result.
 */
inline fun <T, E, Eʹ> Result<T, E>.flatMapFailure(f: (E) -> Result<T, Eʹ>): Result<T, Eʹ> = when (this) {
    is Success<T> -> this
    is Failure<E> -> f(reason)
}

/**
 * Map a function over the _reason_ of an unsuccessful Result.
 */
inline fun <T, E, Eʹ> Result<T, E>.mapFailure(f: (E) -> Eʹ): Result<T, Eʹ> =
    flatMapFailure { reason -> Failure(f(reason)) }

/**
 * Unwrap a Result in which both the success and failure values have the same type, returning a plain value.
 */
fun <T> Result<T, T>.get() = when (this) {
    is Success<T> -> value
    is Failure<T> -> reason
}

/**
 * Unwrap a successful result or throw an exception
 */
fun <T, X: Throwable> Result<T,X>.orThrow() = when (this) {
    is Success<T> -> value
    is Failure<X> -> throw reason
}

/**
 * Unwrap a Result, by returning the success value or calling _block_ on failure to abort from the current function.
 */
inline fun <T, E> Result<T, E>.onFailure(block: (Failure<E>) -> Nothing): T = when (this) {
    is Success<T> -> value
    is Failure<E> -> block(this)
}

/**
 * Unwrap a Result by returning the success value or calling _failureToValue_ to mapping the failure reason to a plain value.
 */
inline fun <S, T : S, U : S, E> Result<T, E>.recover(errorToValue: (E) -> U): S =
    mapFailure(errorToValue).get()

/**
 * Perform a side effect with the success value.
 */
inline fun <T, E> Result<T, E>.peek(f: (T) -> Unit) =
    apply { if (this is Success<T>) f(value) }

/**
 * Perform a side effect with the failure reason.
 */
inline fun <T, E> Result<T, E>.peekFailure(f: (E) -> Unit) =
    apply { if (this is Failure<E>) f(reason) }

