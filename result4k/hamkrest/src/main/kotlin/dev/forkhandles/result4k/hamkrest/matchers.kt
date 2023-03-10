package dev.forkhandles.result4k.hamkrest

import com.natpryce.hamkrest.MatchResult
import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.describe
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result4k
import dev.forkhandles.result4k.Success
import kotlin.reflect.KClass

fun <T> isSuccess(expected: T): Matcher<Result4k<T, *>> = matchValue(Success(expected))
fun isSuccess(): Matcher<Result4k<*, *>> = matchType(Success::class)

fun <E> isFailure(expected: E): Matcher<Result4k<*, E>> = matchValue(Failure(expected))
fun isFailure(): Matcher<Result4k<*, *>> = matchType(Failure::class)

private fun matchValue(expected: Result4k<*, *>) = object : Matcher<Result4k<*, *>> {
    override fun invoke(actual: Result4k<*, *>) = match(actual == expected) { "was: ${describe(actual)}" }
    override val description: String get() = "is ${describe(expected)}"
    override val negatedDescription: String get() = "is not ${describe(expected)}"
}

private fun <T : Result4k<*, *>> matchType(expected: KClass<T>) = object : Matcher<Result4k<*, *>> {
    override fun invoke(actual: Result4k<*, *>) = match(expected.isInstance(actual)) { "was: ${describe(actual)}" }
    override val description: String get() = "is ${expected.simpleName}"
    override val negatedDescription: String get() = "is not ${expected.simpleName}"
}

private inline fun match(comparison: Boolean, describeMismatch: () -> String): MatchResult = when {
    comparison -> MatchResult.Match
    else -> MatchResult.Mismatch(describeMismatch())
}
