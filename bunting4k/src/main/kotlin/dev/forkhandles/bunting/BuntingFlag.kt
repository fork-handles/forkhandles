package dev.forkhandles.bunting

import java.util.UUID
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

data class BuntingFlag<T> internal constructor(
    internal val fn: (String) -> T,
    val description: String? = null,
    internal val default: String?
) : ReadOnlyProperty<Bunting, T> {

    fun <NEXT> map(nextFn: (T) -> NEXT) = BuntingFlag({ nextFn(fn(it)) }, description, default)

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

fun BuntingFlag<String>.int() = map(String::toInt)
fun BuntingFlag<String>.float() = map(String::toFloat)
fun BuntingFlag<String>.long() = map(String::toLong)
fun BuntingFlag<String>.uuid() = map(UUID::fromString)
fun BuntingFlag<String>.char() = map(String::first)
fun BuntingFlag<String>.boolean() = map(String::toBoolean)
inline fun <reified T : Enum<T>> BuntingFlag<String>.enum() =
    copy(description = (description ?: "") + ". Option choice: " + enumValues<T>().toList()).map { enumValueOf<T>(it) }
