@file:Suppress("NonAsciiCharacters")

package dev.forkhandles.result4k

/**
 * A result of a computation that can succeed or fail.
 */
@Suppress("UNCHECKED_CAST")
@JvmInline
value class Result<out T, out E>(private val _value: Any?) {
    fun isFailure(): Boolean = _value is FailureWrapper<*>
    fun isSuccess(): Boolean = !isFailure()

    @PublishedApi
    internal val unsafeValue: T
        get() = when {
            isSuccess() -> _value as T
            else -> error("Attempt to get value on $this")
        }

    @PublishedApi
    internal val unsafeReason: E
        get() = when {
            isSuccess() -> error("Attempt to get reason on $this")
            else -> (_value as FailureWrapper<E>).reason
        }

    internal data class FailureWrapper<F>(internal val reason: F)
}

fun <T> Success(value: T): Result<T, Nothing> = Result(value)
fun <E> Failure(failure: E): Result<Nothing, E> = Result(Result.FailureWrapper(failure))

inline val <E> Result<Nothing, E>.reason: E get() = unsafeReason

inline val <T> Result<T, Nothing>.value: T get() = unsafeValue

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
@Suppress("UNCHECKED_CAST")
inline fun <T, Tʹ, E> Result<T, E>.flatMap(f: (T) -> Result<Tʹ, E>): Result<Tʹ, E> =
    when {
        this.isSuccess() -> f(unsafeValue)
        else -> this as Result<Tʹ, E>
    }

/**
 * Flat-map a function over the _reason_ of a unsuccessful Result.
 */
@Suppress("UNCHECKED_CAST")
inline fun <T, E, Eʹ> Result<T, E>.flatMapFailure(f: (E) -> Result<T, Eʹ>): Result<T, Eʹ> =
    when {
        this.isFailure() -> f(unsafeReason)
        else -> this as Result<T, Eʹ>
    }

/**
 * Map a function over the _reason_ of an unsuccessful Result.
 */
inline fun <T, E, Eʹ> Result<T, E>.mapFailure(f: (E) -> Eʹ): Result<T, Eʹ> =
    flatMapFailure { reason -> Failure(f(reason)) }

/**
 * Unwrap a Result in which both the success and failure values have the same type, returning a plain value.
 */
fun <T> Result<T, T>.get() = when {
    isSuccess() -> unsafeValue
    else -> unsafeReason
}

/**
 * Unwrap a successful result or throw an exception
 */
fun <T, X : Throwable> Result<T, X>.orThrow() = when {
    this.isSuccess() -> unsafeValue
    else -> throw unsafeReason
}

/**
 * Unwrap a Result, by returning the success value or calling _block_ on failure to abort from the current function.
 */
inline fun <T, E> Result<T, E>.onFailure(block: (Result<T, E>) -> Nothing): T = when {
    this.isSuccess() -> unsafeValue
    else -> block(this)
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
    apply { if (this.isSuccess()) f(unsafeValue) }

/**
 * Perform a side effect with the failure reason.
 */
inline fun <T, E> Result<T, E>.peekFailure(f: (E) -> Unit) =
    apply { if (this.isFailure()) f(unsafeReason) }

