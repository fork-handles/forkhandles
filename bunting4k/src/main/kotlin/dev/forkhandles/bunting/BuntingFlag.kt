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
 * Required flags are passed with a value attached and are prefixed with a '-' (short version) or '--' (long version).
 */
data class Required<T> internal constructor(
    internal val fn: (String) -> T,
    override val description: String = ""
) : BuntingFlag<T>(description) {
    override fun getValue(thisRef: Bunting, property: KProperty<*>): T = thisRef.retrieve(property)?.let {
        try {
            fn(it)
        } catch (e: Exception) {
            throw IllegalFlag(property, it, e)
        }
    } ?: throw MissingFlag(property)
}

/**
 * Optional flags are passed with a value attached and are prefixed with a '-' (short version) or '--' (long version).
 */
data class Optional<T> internal constructor(
    internal val fn: (String) -> T,
    override val description: String = "",
    private val default: T? = null
) : BuntingFlag<T?>(description) {

    fun required() = Required(fn, description)

    fun defaultsTo(default: T) = copy(
        default = default,
        description = (description.takeIf { it.isNotBlank() }?.let { "$it. " } ?: "") + "Defaults to \"${default}\"")

    fun <NEXT> map(nextFn: (T) -> NEXT) = Optional({ fn(it)?.let(nextFn) }, description)

    override fun getValue(thisRef: Bunting, property: KProperty<*>) = thisRef.retrieve(property)?.let {
        try {
            fn(it)
        } catch (e: Exception) {
            when (e) {
                is BuntingException -> throw e
                else -> throw IllegalFlag(property, it, e)
            }
        }
    } ?: default
}

private fun Bunting.retrieve(property: KProperty<*>): String? {
    val windowed = args.toList().windowed(2).map { it[0] to it[1] }.toMap()
    return windowed["--${property.name}"] ?: windowed["-${property.name.first()}"]
}

fun Optional<String>.int() = map(String::toInt)
fun Optional<String>.float() = map(String::toFloat)
fun Optional<String>.long() = map(String::toLong)
fun Optional<String>.uuid() = map(UUID::fromString)
fun Optional<String>.char() = map(String::first)
fun Optional<String>.boolean() = map(String::toBoolean)
inline fun <reified T : Enum<T>> Optional<String>.enum() =
    copy(description = description.let { if (it.isNotBlank()) "$it. " else it } + "Option choice: " + enumValues<T>().toList()).map { enumValueOf<T>(it) }
