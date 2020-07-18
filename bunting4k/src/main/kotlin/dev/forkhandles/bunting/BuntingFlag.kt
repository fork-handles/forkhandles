package dev.forkhandles.bunting

import java.util.UUID
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * A value passed on the command line which are normally passed with a `-` (short) or `--`  (long) prefix.
 */
sealed class BuntingFlag<T>(val description: String? = null,
                            internal val masking: (String) -> String = { it }) : ReadOnlyProperty<Bunting, T>

/**
 * Command flags always appear at the start of a command and are not prefixed with a '-' or '--'.
 */
class Command<T : Bunting> internal constructor(private val fn: BuntingConstructor<T>) : BuntingFlag<T?>() {
    override fun getValue(thisRef: Bunting, property: KProperty<*>) =
        if (thisRef.args.firstOrNull() == property.name) fn(thisRef.args.drop(1).toTypedArray()) else null
}

/**
 * Switch flags are optional but not passed with a value attached. They resolve to a boolean.
 */
class Switch internal constructor(description: String?) : BuntingFlag<Boolean>(description) {
    override fun getValue(thisRef: Bunting, property: KProperty<*>): Boolean =
        thisRef.args.contains("--${property.name}") || thisRef.args.contains("-${property.name.first()}")
}

/**
 * Required flags cause a failure when missing..
 */
class Required<T> internal constructor(internal val fn: (String) -> T,
                                       description: String?,
                                       masking: (String) -> String) : BuntingFlag<T>(description, masking) {
    override fun getValue(thisRef: Bunting, property: KProperty<*>): T = thisRef.retrieve(property)?.let {
        try {
            fn(it)
        } catch (e: Exception) {
            throw IllegalFlag(property, masking(it), e)
        }
    } ?: throw MissingFlag(property)
}

/**
 * Optional flags just return null when missing.
 */
class Optional<T> internal constructor(internal val fn: (String) -> T,
                                       description: String?,
                                       masking: (String) -> String,
                                       private val io: IO) : BuntingFlag<T?>(description, masking) {

    fun secret() = Optional(fn, description, { "*".repeat(it.length) }, io)

    fun required() = Required(fn, description, masking)

    fun prompted() = Prompted(fn, description, masking, io, false)

    fun defaultsTo(default: T) = Defaulted(fn,
        (description?.takeIf { it.isNotBlank() }?.let { "$it. " }
            ?: "") + "Defaults to \"${masking(default.toString())}\"",
        masking,
        default)

    fun described(new: String): Optional<T> = Optional(fn, new, masking, io)

    fun <NEXT> map(nextFn: (T) -> NEXT) = Optional({ nextFn(fn(it)) }, description, masking, io)

    override fun getValue(thisRef: Bunting, property: KProperty<*>) = thisRef.retrieve(property)?.let {
        try {
            fn(it)
        } catch (e: Exception) {
            throw IllegalFlag(property, it, e)
        }
    }
}

/**
 * Defaulted flags fall back to a passed value when missing.
 */
class Defaulted<T> internal constructor(internal val fn: (String) -> T,
                                        description: String?,
                                        output: (String) -> String,
                                        private val default: T) : BuntingFlag<T>(description, output) {

    override fun getValue(thisRef: Bunting, property: KProperty<*>): T = thisRef.retrieve(property)?.let {
        try {
            fn(it)
        } catch (e: Exception) {
            throw IllegalFlag(property, masking(it), e)
        }
    } ?: default
}

/**
 * Prompted flags cause a prompt to be displayed to the user when missing.
 */
class Prompted<T> internal constructor(internal val fn: (String) -> T,
                                       description: String?,
                                       masking: (String) -> String,
                                       private val io: IO,
                                       private val masked: Boolean
) : BuntingFlag<T>(description, masking) {

    override fun getValue(thisRef: Bunting, property: KProperty<*>): T =
        with(thisRef.retrieve(property) ?: promptForValue()) {
            try {
                fn(this)
            } catch (e: Exception) {
                throw IllegalFlag(property, masking(this), e)
            }
        }

    private fun promptForValue() = io.run {
        write("Enter value for \"$description\": ")
        read(masked)
    }
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
fun Optional<String>.boolean() = map {
    when {
        it.toBoolean() -> it.toBoolean()
        it.toLowerCase() != false.toString() -> throw IllegalArgumentException()
        else -> false
    }
}

inline fun <reified T : Enum<T>> Optional<String>.enum() =
    described((description?.let { "$it. " } ?: "")
        + "Option choice: " + enumValues<T>().toList()).map { enumValueOf<T>(it) }
