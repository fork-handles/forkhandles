package dev.forkhandles.mock4k

import dev.forkhandles.mock4k.MockMode.Relaxed
import dev.forkhandles.mock4k.MockMode.Strict
import java.lang.reflect.Proxy
import java.lang.reflect.UndeclaredThrowableException
import kotlin.reflect.KClass

inline fun <reified T> mock(relaxed: MockMode = Strict): T = Proxy.newProxyInstance(
    T::class.java.classLoader,
    arrayOf(T::class.java)
) { _, method, args ->
    when (relaxed) {
        Strict -> throw UnstubbedCall(T::class, method.name, args.toList())
        Relaxed -> null
    }
} as T

class UnstubbedCall(target: KClass<*>, method: String, args: List<Any>) : UndeclaredThrowableException(
    null,
    "Unstubbed call: ${target.simpleName}.$method(${args.joinToString(", ")})"
)
