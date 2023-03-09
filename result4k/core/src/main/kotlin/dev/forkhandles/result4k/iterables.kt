package dev.forkhandles.result4k


fun <T, E> Iterable<Result<T, E>>.allValues(): Result<List<T>, E> =
    Success(map { r -> r.onFailure { return it } })

fun <T, E> Iterable<Result<T, E>>.anyValues(): List<T> =
    filterIsInstance<Success<T>>().map { it.value }

fun <T, E> Iterable<Result<T, E>>.partition(): Pair<List<T>, List<E>> {
    val oks = mutableListOf<T>()
    val errs = mutableListOf<E>()
    forEach {
        when (it) {
            is Success<T> -> oks.add(it.value)
            is Failure<E> -> errs.add(it.reason)
        }
    }
    return Pair(oks, errs)
}
