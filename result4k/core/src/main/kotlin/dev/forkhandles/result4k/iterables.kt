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

// Traverse family of functions

fun <T, Tʹ, E> Iterable<T>.foldResult(
    initial: Result<Tʹ, E>,
    operation: (acc: Tʹ, T) -> Result<Tʹ, E>
): Result<Tʹ, E> =
    fold(initial) { acc, el -> acc.flatMap { accVal -> operation(accVal, el) } }

fun <T, Tʹ, E> Sequence<T>.foldResult(
    initial: Result<Tʹ, E>,
    operation: (acc: Tʹ, T) -> Result<Tʹ, E>
): Result<Tʹ, E> =
    fold(initial) { acc, el -> acc.flatMap { accVal -> operation(accVal, el) } }

fun <T, Tʹ, E> Iterable<T>.mapAllValues(f: (T) -> Result<Tʹ, E>): Result<List<Tʹ>, E> {
    return mutableListOf<Tʹ>()
        .also { results -> forEach { e -> results.add(f(e).onFailure { return it }) } }
        .let(::Success)
}

fun <T, Tʹ, E> Sequence<T>.mapAllValues(f: (T) -> Result<Tʹ, E>): Result<List<Tʹ>, E> {
    return mutableListOf<Tʹ>()
        .also { results -> forEach { e -> results.add(f(e).onFailure { return it }) } }
        .let(::Success)
}

fun <T, E> Sequence<Result<T, E>>.allValues(): Result<List<T>, E> =
    mapAllValues { it }
