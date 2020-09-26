package dev.forkhandles.bunting

import dev.forkhandles.bunting.Visibility.Public
import dev.forkhandles.bunting.Visibility.Secret
import java.util.UUID
import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

/**
 * A value passed on the command line which are normally passed with a `-` (short) or `--`  (long) prefix.
 */
sealed class BuntingFlag<T>(val description: String? = null,
                            internal val visibility: Visibility = Public) : ReadOnlyProperty<Bunting, T>

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
                                       visibility: Visibility) : BuntingFlag<T>(description, visibility) {
    override fun getValue(thisRef: Bunting, property: KProperty<*>): T = thisRef.retrieve(property)?.let {
        try {
            fn(it)
        } catch (e: Exception) {
            throw IllegalFlag(property, visibility(it), e)
        }
    } ?: throw MissingFlag(property)
}

enum class Visibility : (String) -> String {
    Public {
        override operator fun invoke(value: String): String = value
    },
    Secret {
        override operator fun invoke(value: String): String = "*".repeat(value.length)
    };
}

/**
 * Optional flags just return null when missing.
 */
class Optional<T> internal constructor(internal val fn: (String) -> T,
                                       description: String?,
                                       visibility: Visibility,
                                       private val config: Config,
                                       private val io: IO) : BuntingFlag<T?>(description, visibility) {

    fun secret() = Optional(fn, description, Secret, config, io)

    fun required() = Required(fn, description, visibility)

    fun prompted() = Prompted(fn, description, visibility, io)

    fun defaultsTo(default: T) = Defaulted(fn,
        (description?.takeIf { it.isNotBlank() }?.let { "$it. " }
            ?: "") + "Defaults to \"${visibility(default.toString())}\"",
        visibility,
        { default })

    fun configuredAs(configProperty: String) = Configured(fn,
        (description?.takeIf { it.isNotBlank() }?.let { "$it. " }
            ?: "") + "Configured as \"$configProperty\"",
        visibility,
        configProperty,
        config)

    fun described(new: String): Optional<T> = Optional(fn, new, visibility, config, io)

    fun <NEXT> map(nextFn: (T) -> NEXT) = Optional({ nextFn(fn(it)) }, description, visibility, config, io)

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
                                        visibility: Visibility,
                                        private val default: () -> T) : BuntingFlag<T>(description, visibility) {

    override fun getValue(thisRef: Bunting, property: KProperty<*>): T = thisRef.retrieve(property)?.let {
        try {
            fn(it)
        } catch (e: Exception) {
            throw IllegalFlag(property, visibility(it), e)
        }
    } ?: default()
}

/**
 * Configured flags are stored in config file.
 */
class Configured<T> internal constructor(internal val fn: (String) -> T,
                                         description: String?,
                                         visibility: Visibility,
                                         private val configProperty: String,
                                         private val config: Config) : BuntingFlag<T?>(description, visibility) {

    override fun getValue(thisRef: Bunting, property: KProperty<*>): T? =
        (thisRef.retrieve(property) ?: config[configProperty])?.let {
            try {
                fn(it)
            } catch (e: Exception) {
                throw IllegalFlag(property, visibility(it), e)
            }
        }


    fun defaultsTo(default: T) = Defaulted(fn,
        (description?.takeIf { it.isNotBlank() }?.let { "$it. " }
            ?: "") + "Defaults to \"${visibility(default.toString())}\"",
        visibility,
        { config[configProperty] = default.toString(); default })
}

/**
 * Prompted flags cause a prompt to be displayed to the user when missing.
 */
class Prompted<T> internal constructor(internal val fn: (String) -> T,
                                       description: String?,
                                       visibility: Visibility,
                                       private val io: IO
) : BuntingFlag<T>(description, visibility) {

    override fun getValue(thisRef: Bunting, property: KProperty<*>): T =
        with(thisRef.retrieve(property) ?: promptForValue()) {
            try {
                fn(this)
            } catch (e: Exception) {
                throw IllegalFlag(property, visibility(this), e)
            }
        }

    private fun promptForValue() = io.run {
        write("Enter value for \"$description\": ")
        read(visibility)
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
