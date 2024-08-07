package dev.forkhandles.result4k.strikt

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import strikt.api.Assertion
import strikt.assertions.isA
import strikt.assertions.isEqualTo

fun Assertion.Builder<*>.isSuccess() =
    isA<Success<*>>()

fun Assertion.Builder<*>.isFailure() =
    isA<Failure<*>>()

@JvmName("isSuccessOfInstance")
inline fun <reified T> Assertion.Builder<*>.isSuccess() =
    isA<Success<T>>().and { get { value }.isA<T>() }

@JvmName("isFailureOfInstance")
inline fun <reified E> Assertion.Builder<*>.isFailure() =
    isA<Failure<E>>().and { get { reason }.isA<E>() }

@JvmName("isSuccessOfInstanceAndValue")
inline fun <reified T> Assertion.Builder<*>.isSuccess(expected: T) =
    isA<Success<T>>().and { get { value }.isEqualTo(expected) }

@JvmName("isFailureOfInstanceAndValue")
inline fun <reified E> Assertion.Builder<*>.isFailure(expected: E) =
    isA<Failure<E>>().and { get { reason }.isEqualTo(expected) }

