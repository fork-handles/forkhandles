package dev.forkhandles.result4k.hamkrest

import com.natpryce.hamkrest.MatchResult
import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.has
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success

fun <T> isSuccess(expected: T): Matcher<Result4k<T, *>> = isSuccess(equalTo(expected))
fun <T> isSuccess(matcher: Matcher<T>): Matcher<Result4k<T, *>> =
    isA<Success<T>>(has("value", Success<T>::value, matcher))
fun <T> isSuccess(): Matcher<Result4k<T, *>> = isA<Success<T>>()
fun <E> isFailure(expected: E): Matcher<Result4k<*, E>> = isFailure(equalTo(expected))
fun <E> isFailure(matcher: Matcher<E>): Matcher<Result4k<*, E>> =
    isA<Failure<E>>(has("reason", Failure<E>::reason, matcher))

fun <E> isFailure(): Matcher<Result4k<*, E>> = isA<Failure<E>>()

// same as hamkrest's isA, but only prints the simple name of the class instead of the fully qualified name
private inline fun <reified T : Result4k<*, *>> isA(downcastMatcher: Matcher<T>? = null) = object : Matcher<Any> {
    override fun invoke(actual: Any) =
        if (actual !is T) {
            MatchResult.Mismatch("was: $actual")
        } else if (downcastMatcher == null) {
            MatchResult.Match
        } else {
            downcastMatcher(actual)
        }

    override val description: String
        get() = "is a ${T::class.simpleName}" + if (downcastMatcher == null) "" else " and ${downcastMatcher.description}"
}
