package dev.forkhandles.values

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.resultFrom

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

    fun parse(value: String) = validate(parseFn(value))

    @Deprecated("use show()", ReplaceWith("show(value)"))
    fun print(value: DOMAIN) = show(value)

    fun show(value: DOMAIN) = showFn(value.value)

    fun of(value: PRIMITIVE) = validate(value)
}

/**
 * Return a Object/null based on validation.
 */
fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.ofOrNull(value: PRIMITIVE): DOMAIN? =
    try {
        validate(value)
    } catch (e: Exception) {
        null
    }

/**
 * Parse a Object/null based on validation.
 */
fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.parseOrNull(value: String): DOMAIN? =
    try {
        validate(parseFn(value))
    } catch (e: Exception) {
        null
    }

/**
 * Return a Result4k Success/Failure based on validation.
 */
fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.ofResult4k(value: PRIMITIVE): Result<DOMAIN, Exception> =
    resultFrom { validate(value) }

/**
 * Return a Result4k Success/Failure based on validation.
 */
fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.parseResult4k(value: String): Result<DOMAIN, Exception> =
    resultFrom { validate(parseFn(value)) }

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.ofList(vararg values: PRIMITIVE) =
    values.map(::of)

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.ofListOrNull(vararg values: PRIMITIVE) =
    when {
        values.isEmpty() -> emptyList()
        else -> values.mapNotNull(::ofOrNull).takeIf(List<DOMAIN>::isNotEmpty)
    }

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.ofListResult4k(vararg values: PRIMITIVE):
    Result<List<DOMAIN>, Exception> =
    when {
        values.isEmpty() -> Success(emptyList())
        else ->
            values.drop(1)
                .fold(ofResult4k(values.first()).map(::listOf)) { acc, next ->
                    when (acc) {
                        is Success -> ofResult4k(next).map { acc.value + it }
                        is Failure -> acc
                    }
                }
    }

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.parseList(vararg values: String) =
    values.map(::parse)

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.parseListOrNull(vararg values: String) =
    values.map(::parseOrNull)

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.parseListResult4k(vararg values: String) =
    values.map(::parseResult4k)

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.showList(vararg values: DOMAIN) =
    values.map(::show)
