package dev.forkhandles.bunting

import java.util.UUID
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

sealed class BuntingFlag<T>(open val description: String? = null) : ReadOnlyProperty<Bunting, T>

class NoValueFlag(description: String? = null) : BuntingFlag<Boolean>(description) {
    override fun getValue(thisRef: Bunting, property: KProperty<*>): Boolean =
        thisRef.args.contains("--${property.name}") || thisRef.args.contains("--${property.name.first()}")
}

data class ValueFlag<T> internal constructor(
    internal val fn: (String) -> T,
    override val description: String? = null,
    internal val default: String?
) : BuntingFlag<T>(description) {

    fun <NEXT> map(nextFn: (T) -> NEXT) = ValueFlag({ nextFn(fn(it)) }, description, default)

    override fun getValue(thisRef: Bunting, property: KProperty<*>): T {
        val windowed = thisRef.args.toList().windowed(2).map { it[0] to it[1] }.toMap()

        val init = windowed["--${property.name}"] ?: windowed["-${property.name.first()}"] ?: default

        return init?.let {
            try {
                fn(it) ?: throw MissingFlag(property)
            } catch (e: Exception) {
                throw IllegalFlag(property, it, e)
            }
        } ?: throw MissingFlag(property)
    }
}

fun ValueFlag<String>.int() = map(String::toInt)
fun ValueFlag<String>.float() = map(String::toFloat)
fun ValueFlag<String>.long() = map(String::toLong)
fun ValueFlag<String>.uuid() = map(UUID::fromString)
fun ValueFlag<String>.char() = map(String::first)
fun ValueFlag<String>.boolean() = map(String::toBoolean)
inline fun <reified T : Enum<T>> ValueFlag<String>.enum() =
    copy(description = (description ?: "") + ". Option choice: " + enumValues<T>().toList()).map { enumValueOf<T>(it) }
