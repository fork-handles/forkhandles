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

fun <T, Tʹ, E> Iterable<T>.traverse(f: (T) -> Result<Tʹ, E>): Result<List<Tʹ>, E> =
    foldResult(Success(mutableListOf())) { acc, e ->
        f(e).map { acc.add(it); acc }
    }

fun <T, E> Iterable<Result<T, E>>.extractList(): Result<List<T>, E> =
    traverse { it }

fun <T, Tʹ, E> Sequence<T>.foldResult(
    initial: Result<Tʹ, E>,
    operation: (acc: Tʹ, T) -> Result<Tʹ, E>
): Result<Tʹ, E> {
    val iter = iterator()

    tailrec fun loop(acc: Result<Tʹ, E>): Result<Tʹ, E> =
        if (!iter.hasNext()) acc
        else when (acc) {
            is Success -> loop(operation(acc.value, iter.next()))
            is Failure -> acc
        }

    return loop(initial)
}

fun <T, Tʹ, E> Sequence<T>.traverse(f: (T) -> Result<Tʹ, E>): Result<List<Tʹ>, E> =
    foldResult(Success(mutableListOf())) { acc, e ->
        f(e).map { acc.add(it); acc }
    }

fun <T, E> Sequence<Result<T, E>>.extractList(): Result<List<T>, E> =
    traverse { it }
