package dev.forkhandles.mock4k

import java.lang.reflect.Proxy
import java.lang.reflect.UndeclaredThrowableException
import kotlin.reflect.KClass

inline fun <reified T> mock(): T = Proxy.newProxyInstance(
    T::class.java.classLoader,
    arrayOf(T::class.java)
) { _, method, args -> throw UnstubbedCall(T::class, method.name, args.toList()) } as T

class UnstubbedCall(target: KClass<*>, method: String, args: List<Any>) : UndeclaredThrowableException(
    null,
    "Unstubbed call: ${target.simpleName}.$method(${args.joinToString(", ")})"
)
