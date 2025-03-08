package dev.forkhandles.result4k


inline fun <T, E> Result<T, E>.retainIf(test: (T) -> Boolean, otherwise: () -> E): Result<T,E> =
    flatMap { if (test(it)) Success(it) else Failure(otherwise()) }

inline fun <T, E> Result<T, E>.rejectIf(test: (T) -> Boolean, error: () -> E): Result<T,E> =
    retainIf({!test(it)}, error)
