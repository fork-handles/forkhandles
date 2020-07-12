package dev.forkhandles.bunting

import java.util.UUID
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * A value passed on the command line.
 */
sealed class BuntingFlag<T>(open val description: String? = null) : ReadOnlyProperty<Bunting, T>

/**
 * Command flags always appear at the start of a command and are not prefixed with a '-' or '--'.
 */
class Command<T : Bunting>(private val fn: (Array<String>) -> T) : BuntingFlag<T?>() {
    override fun getValue(thisRef: Bunting, property: KProperty<*>) =
        if (thisRef.args.firstOrNull() == property.name) fn(thisRef.args.drop(1).toTypedArray()) else null
}

/**
 * Switch flags are not passed with a value attached and are prefixed with a '-' (short version) or '--' (long version).
 */
class Switch(description: String = "") : BuntingFlag<Boolean>(description) {
    override fun getValue(thisRef: Bunting, property: KProperty<*>): Boolean =
        thisRef.args.contains("--${property.name}") || thisRef.args.contains("-${property.name.first()}")
}

/**
 * Option flags are passed with a value attached and are prefixed with a '-' (short version) or '--' (long version).
 */
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
                throw IllegalFlag(property, it, e)
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
    copy(description = description.let { if(it.isNotBlank()) "$it. " else it } + "Option choice: " + enumValues<T>().toList()).map { enumValueOf<T>(it) }
