package dev.forkhandles.values

/**
 * Base value type for inline classes which enables type-safe primitives, along with Validation.
 */
abstract class ValueFactory<DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any>(
    internal val coerceFn: (PRIMITIVE) -> DOMAIN,
    private val validation: Validation<PRIMITIVE>? = null,
    internal val parseFn: (String) -> PRIMITIVE,
    internal val showFn: (PRIMITIVE) -> String = { it.toString() },
    internal val onInvalid: ValueFactory<DOMAIN, PRIMITIVE>.(PRIMITIVE, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    internal val onParseFailure: ValueFactory<DOMAIN, PRIMITIVE>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
) {
    internal fun validate(value: PRIMITIVE): DOMAIN {
        validation?.check(value)
        return coerceFn(value)
    }

    @Deprecated("Use of()", ReplaceWith("of(value)"))
    operator fun invoke(value: PRIMITIVE): Any = error("invoke() factory method is not to be used for building microtypes -  use of() instead!")

    open fun parse(value: String): DOMAIN {
        val parsed = attempt({ onParseFailure(value, it) }) { parseFn(value) }
        return attempt({ onInvalid(parsed, it) }) { validate(parsed) }
    }

    fun show(value: DOMAIN) = showFn(unwrap(value))

    open fun of(value: PRIMITIVE) = attempt({ onInvalid(value, it) }) { validate(value) }

    fun unwrap(value: DOMAIN) = value.value

    private fun <T> attempt(onError: (Exception) -> Nothing, value: () -> T) = try {
        value()
    } catch (e: Exception) {
        onError(e)
    }
}

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.ofList(vararg values: PRIMITIVE) =
    values.map(::of)

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.parseList(vararg values: String) =
    values.map(::parse)

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.showList(vararg values: DOMAIN) =
    showList(values.toList())

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.showList(values: List<DOMAIN>) =
    values.map(::show)

internal fun ValueFactory<*, *>.defaultOnInvalid(e: Exception): Nothing {
    throw IllegalArgumentException(
        this::class.java.name.substringBeforeLast('$') +
            ": " + e::class.java.name + " " + e.localizedMessage
    )
}
