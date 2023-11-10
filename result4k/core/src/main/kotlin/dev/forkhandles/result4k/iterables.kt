package dev.forkhandles.result4k

fun <T, E> Iterable<Result<T, E>>.allValues(): Result<List<T>, E> =
    mapAllValues { it }

fun <T, E> Sequence<Result<T, E>>.allValues(): Result<List<T>, E> =
    asIterable().allValues()

fun <T, E> Iterable<Result<T, E>>.anyValues(): List<T> =
    filterIsInstance<Success<T>>().map { it.value }

fun <T, E> Sequence<Result<T, E>>.anyValues(): Sequence<T> =
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

fun <T, E> Sequence<Result<T, E>>.partition(): Pair<List<T>, List<E>> =
    asIterable().partition()

fun <T, Tʹ, E> Iterable<T>.foldResult(
    initial: Result<Tʹ, E>,
    operation: (acc: Tʹ, T) -> Result<Tʹ, E>
): Result<Tʹ, E> =
    fold(initial) { acc, el -> acc.flatMap { accVal -> operation(accVal, el) } }

fun <T, Tʹ, E> Sequence<T>.foldResult(
    initial: Result<Tʹ, E>,
    operation: (acc: Tʹ, T) -> Result<Tʹ, E>
): Result<Tʹ, E> =
    asIterable().foldResult(initial, operation)

fun <T, Tʹ, E> Iterable<T>.mapAllValues(f: (T) -> Result<Tʹ, E>): Result<List<Tʹ>, E> =
    map { e -> f(e).onFailure { return it } }
        .let(::Success)

fun <T, Tʹ, E> Sequence<T>.mapAllValues(f: (T) -> Result<Tʹ, E>): Result<List<Tʹ>, E> =
    asIterable().mapAllValues(f)
