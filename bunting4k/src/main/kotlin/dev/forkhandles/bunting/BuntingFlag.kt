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
class Command<T : Bunting>(private val fn: BuntingConstructor<T>) : BuntingFlag<T?>() {
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
class Required<T> internal constructor(internal val fn: (String) -> T,
                                       override val description: String = "",
                                       private val output: (String) -> String) : BuntingFlag<T>(description) {
    override fun getValue(thisRef: Bunting, property: KProperty<*>): T = thisRef.retrieve(property)?.let {
        try {
            fn(it)
        } catch (e: Exception) {
            throw IllegalFlag(property, output(it), e)
        }
    } ?: throw MissingFlag(property)
}

/**
 * Optional flags are passed with a value attached and are prefixed with a '-' (short version) or '--' (long version).
 */
data class Optional<T> internal constructor(internal val fn: (String) -> T,
                                            override val description: String,
                                            private val output: (String) -> String,
                                            private val io: IO) : BuntingFlag<T?>(description) {

    fun secret() = copy(output = { "*".repeat(it.length) })

    fun required() = Required(fn, description, output)

    fun prompted() = Prompted(fn, description, output, io, false)

    fun defaultsTo(default: T) = Defaulted(fn,
        (description.takeIf { it.isNotBlank() }?.let { "$it. " }
            ?: "") + "Defaults to \"${output(default.toString())}\"",
        output,
        default)

    fun <NEXT> map(nextFn: (T) -> NEXT) = Optional({ nextFn(fn(it)) }, description, output, io)

    override fun getValue(thisRef: Bunting, property: KProperty<*>) = thisRef.retrieve(property)?.let {
        try {
            fn(it)
        } catch (e: Exception) {
            throw IllegalFlag(property, it, e)
        }
    }
}

/**
 * Defaulted flags are passed with a value attached and are prefixed with a '-' (short version) or '--' (long version).
 */
data class Defaulted<T> internal constructor(internal val fn: (String) -> T,
                                             override val description: String,
                                             private val output: (String) -> String,
                                             private val default: T) : BuntingFlag<T>(description) {

    override fun getValue(thisRef: Bunting, property: KProperty<*>): T = thisRef.retrieve(property)?.let {
        try {
            fn(it)
        } catch (e: Exception) {
            throw IllegalFlag(property, output(it), e)
        }
    } ?: default
}

/**
 * Defaulted flags cause a prompt from user if missing and are prefixed with a '-' (short version) or '--' (long version).
 */
data class Prompted<T> internal constructor(internal val fn: (String) -> T,
                                            override val description: String,
                                            private val output: (String) -> String,
                                            private val io: IO,
                                            private val masked: Boolean
) : BuntingFlag<T>(description) {

    override fun getValue(thisRef: Bunting, property: KProperty<*>): T =
        with(thisRef.retrieve(property) ?: promptForValue()) {
            try {
                fn(this)
            } catch (e: Exception) {
                throw IllegalFlag(property, output(this), e)
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
    copy(description = description.let { if (it.isNotBlank()) "$it. " else it } + "Option choice: " + enumValues<T>().toList()).map { enumValueOf<T>(it) }
