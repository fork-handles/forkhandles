package dev.forkhandles.result4k

inline fun <T, E> Result<T, E>.retainIf(test: (T) -> Boolean, otherwise: (T) -> E): Result<T, E> =
    flatMap { if (test(it)) Success(it) else Failure(otherwise(it)) }

inline fun <T, E> Result<T, E>.rejectIf(test: (T) -> Boolean, error: (T) -> E): Result<T, E> =
    retainIf({ !test(it) }, error)

inline fun <T, E> Result<T, E>.retainIf(b: Boolean, otherwise: () -> E): Result<T, E> =
    flatMap { if (b) Success(it) else Failure(otherwise()) }

inline fun <T, E> Result<T, E>.rejectIf(b: Boolean, error: () -> E): Result<T, E> =
    retainIf(!b, error)

inline fun <T, E> Result<T, E>.retainIfNotNull(otherwise: (T) -> E): Result<T, E> =
    flatMap { it?.asSuccess() ?: Failure(otherwise(it)) }

inline fun <T, E> Result<T, E>.rejectIfNull(otherwise: (T) -> E): Result<T, E> =
    flatMap { it?.asSuccess() ?: Failure(otherwise(it)) }
