package dev.forkhandles.values

/**
 * Base value type for inline classes which enables type-safe primitives, along with Validation.
 */
abstract class ValueFactory<DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any>(
    internal val coerceFn: (PRIMITIVE) -> DOMAIN,
    private val validation: Validation<PRIMITIVE>? = null,
    internal val parseFn: (String) -> PRIMITIVE,
    internal val showFn: (PRIMITIVE) -> String = { it.toString() }
) {
    internal fun validate(value: PRIMITIVE): DOMAIN {
        validation?.check(value)
        return coerceFn(value)
    }

    @Deprecated("Use of()", ReplaceWith("of(value)"))
    operator fun invoke(value: PRIMITIVE): Any = error("invoke() factory method is not to be used for building microtypes -  use of() instead!")

    fun parse(value: String) = attempt { validate(parseFn(value)) }

    @Deprecated("use show()", ReplaceWith("show(value)"))
    fun print(value: DOMAIN) = show(value)

    fun show(value: DOMAIN) = showFn(unwrap(value))

    fun of(value: PRIMITIVE) = attempt { validate(value) }

    fun unwrap(value: DOMAIN) = value.value

    private fun <T> attempt(value: () -> T) = try {
        value()
    } catch (e: Exception) {
        throw IllegalArgumentException(
            this::class.java.name.substringBeforeLast('$') +
                ": " + e::class.java.name + " " + e.localizedMessage
        )
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
