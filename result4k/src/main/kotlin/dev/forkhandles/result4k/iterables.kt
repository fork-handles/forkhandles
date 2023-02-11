package dev.forkhandles.result4k


@Suppress("UNCHECKED_CAST")
fun <T, E> Iterable<Result<T, E>>.allValues(): Result<List<T>, E> =
    Success(map { r -> r.onFailure { return it as Result<List<T>, E> } })

fun <T, E> Iterable<Result<T, E>>.anyValues(): List<T> =
    filter { it.isSuccess() }.map { it.unsafeValue }

fun <T, E> Iterable<Result<T, E>>.partition(): Pair<List<T>, List<E>> {
    val oks = mutableListOf<T>()
    val errs = mutableListOf<E>()
    forEach {
        when {
            it.isSuccess() -> oks.add(it.unsafeValue)
            else -> errs.add(it.unsafeReason)
        }
    }
    return Pair(oks, errs)
}
