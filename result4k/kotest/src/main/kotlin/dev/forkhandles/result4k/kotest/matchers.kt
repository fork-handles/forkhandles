package dev.forkhandles.result4k.kotest

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import io.kotest.matchers.ComparableMatcherResult
import io.kotest.matchers.Matcher
import io.kotest.matchers.MatcherResult
import io.kotest.matchers.should
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract
import kotlin.reflect.KClass

fun <T> beSuccess(expected: T): Matcher<Result<T, *>> = object : Matcher<Result<T, *>> {
    override fun test(value: Result<T, *>) = Success(expected).matchesValue(value)
}

fun beSuccess(): Matcher<Result<*, *>> = object : Matcher<Result<*, *>> {
    override fun test(value: Result<*, *>) = value.matchesType(Success::class)
}

@OptIn(ExperimentalContracts::class)
fun <T> Result<T, *>.shouldBeSuccess(): T {
    contract {
        returns() implies (this@shouldBeSuccess is Success<T>)
    }
    this should beSuccess()
    return (this as Success).value
}

infix fun <T> Result<T, *>.shouldBeSuccess(block: (T) -> Unit) {
    block(this.shouldBeSuccess())
}

infix fun <T> Result<T, *>.shouldBeSuccess(value: T) = this should beSuccess(value)

fun <E> beFailure(expected: E): Matcher<Result<*, E>> = object : Matcher<Result<*, E>> {
    override fun test(value: Result<*, E>) = Failure(expected).matchesValue(value)
}

fun beFailure(): Matcher<Result<*, *>> = object : Matcher<Result<*, *>> {
    override fun test(value: Result<*, *>) = value.matchesType(Failure::class)
}

@OptIn(ExperimentalContracts::class)
fun <E> Result<*, E>.shouldBeFailure(): E {
    contract {
        returns() implies (this@shouldBeFailure is Failure<*>)
    }
    this should beFailure()
    return (this as Failure).reason
}

infix fun <E> Result<*, E>.shouldBeFailure(block: (E) -> Unit) {
    this.shouldBeFailure()
    block((this as Failure<E>).reason)
}

infix fun <E> Result<*, E>.shouldBeFailure(expected: E) =
    this should beFailure(expected)

private fun <T, E> Result<T, E>.matchesValue(actual: Result<T, E>): MatcherResult =
    matcherResultWithIntelliJDiff(
        passed = this == actual,
        actual = actual.toString(),
        expected = this.toString()
    )

private fun <T, E, C : Result<T, E>> Result<T, E>.matchesType(expected: KClass<C>): MatcherResult =
    matcherResultWithIntelliJDiff(
        passed = expected.isInstance(this),
        actual = this.toString(),
        expected = expected.simpleName!!
    )

/**
 * Return ComparableMatcherResult so that Kotest throws AssertFailedError
 * with the failure message formatted by io.kotest.assertions.intellijFormatError()
 * which makes IntelliJ show the link to the diff window.
 */
private fun matcherResultWithIntelliJDiff(passed: Boolean, actual: String, expected: String) =
    ComparableMatcherResult(
        passed = passed,
        failureMessageFn = { "" },
        negatedFailureMessageFn = { "not " },
        actual = actual,
        expected = expected
    )
