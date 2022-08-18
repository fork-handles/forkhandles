package dev.forkhandles.result4k

/*
 * Translate between the Result and Nullable/Optional/Maybe monads
 */

/**
 * Convert a nullable value to a Result, using the result of [failureDescription] as the failure reason
 * if the value is null.
 */
inline fun <T, E> T?.asResultOr(failureDescription: () -> E) =
    if (this != null) Success(this) else Failure(failureDescription())

/**
 * Convert a Success of a nullable value to a Success of a non-null value or a Failure,
 * using the result of [failureDescription] as the failure reason, if the value is null.
 */
inline fun <T : Any, E> Result<T?, E>.filterNotNull(failureDescription: () -> E) =
    flatMap { it.asResultOr(failureDescription) }

/**
 * Returns the success value, or null if the Result is a failure.
 */
fun <T, E> Result<T, E>.valueOrNull() = when {
    isSuccess() -> unsafeValue
    else -> null
}

/**
 * Returns the failure reason, or null if the Result is a success.
 */
fun <T, E> Result<T, E>.failureOrNull() = when {
    isFailure() -> unsafeReason
    else -> null
}
