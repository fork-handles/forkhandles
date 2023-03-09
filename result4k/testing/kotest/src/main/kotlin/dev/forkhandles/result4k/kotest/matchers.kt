package dev.forkhandles.result4k.kotest

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
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
fun <T> Result<T, *>.shouldBeSuccess() {
    contract {
        returns() implies (this@shouldBeSuccess is Success<*>)
    }
    this should beSuccess()
}

infix fun <T> Result<T, *>.shouldBeSuccess(block: (T) -> Unit) {
    this.shouldBeSuccess()
    block((this as Success<T>).value)
}

infix fun <T> Result<T, *>.shouldBeSuccess(value: T) = this should beSuccess(value)

fun <E> beFailure(expected: E): Matcher<Result<*, E>> = object : Matcher<Result<*, E>> {
    override fun test(value: Result<*, E>) = Failure(expected).matchesValue(value)
}

fun beFailure(): Matcher<Result<*, *>> = object : Matcher<Result<*, *>> {
    override fun test(value: Result<*, *>) = value.matchesType(Failure::class)
}

@OptIn(ExperimentalContracts::class)
fun <E> Result<*, E>.shouldBeFailure() {
    contract {
        returns() implies (this@shouldBeFailure is Failure<*>)
    }
    this should beFailure()
}

infix fun <E> Result<*, E>.shouldBeFailure(block: (E) -> Unit) {
    this.shouldBeFailure()
    block((this as Failure<E>).reason)
}

infix fun <E> Result<*, E>.shouldBeFailure(expected: E) =
    this should beFailure(expected)

private fun <T, E> Result<T, E>.matchesValue(value: Result<T, E>): MatcherResult =
    MatcherResult(
        value == this,
        { "$value should be $this" },
        { "$value should not be $this" },
    )

private fun <T, E, C : Result<T, E>> Result<T, E>.matchesType(resultClass: KClass<C>): MatcherResult =
    MatcherResult(
        resultClass.isInstance(this),
        { "$this should be ${resultClass.simpleName}" },
        { "$this should not be ${resultClass.simpleName}" },
    )
