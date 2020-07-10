package dev.forkhandles.bunting

import java.util.UUID
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

sealed class BuntingFlag<T>(open val description: String = "") : ReadOnlyProperty<Bunting, T>

class Switch(description: String = "") : BuntingFlag<Boolean>(description) {
    override fun getValue(thisRef: Bunting, property: KProperty<*>): Boolean =
        thisRef.args.contains("--${property.name}") || thisRef.args.contains("-${property.name.first()}")
}

data class Option<T> internal constructor(
    internal val fn: (String) -> T,
    override val description: String = "",
    internal val default: String?
) : BuntingFlag<T>(description) {

    fun defaultsTo(default: String) = copy(default = default, description =
    (description.takeIf { it.isNotBlank() }?.let { "$it. " } ?: "") + "Defaults to \"${default}\"")

    fun <NEXT> map(nextFn: (T) -> NEXT) = Option({ nextFn(fn(it)) }, description, default)

    override fun getValue(thisRef: Bunting, property: KProperty<*>): T {
        val windowed = thisRef.args.toList().windowed(2).map { it[0] to it[1] }.toMap()

        val init = windowed["--${property.name}"] ?: windowed["-${property.name.first()}"] ?: default

        return init?.let {
            try {
                fn(it) ?: throw MissingFlag(property)
            } catch (e: Exception) {
                throw IllegalFlag(property, "$it. $description", e)
            }
        } ?: throw MissingFlag(property)
    }
}

fun Option<String>.int() = map(String::toInt)
fun Option<String>.float() = map(String::toFloat)
fun Option<String>.long() = map(String::toLong)
fun Option<String>.uuid() = map(UUID::fromString)
fun Option<String>.char() = map(String::first)
fun Option<String>.boolean() = map(String::toBoolean)
inline fun <reified T : Enum<T>> Option<String>.enum() =
    copy(description = description + ". Option choice: " + enumValues<T>().toList()).map { enumValueOf<T>(it) }
