package dev.forkhandles.values

/**
 * Return a Object/null based on validation.
 */

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.ofOrNull(value: PRIMITIVE) =
    try {
        validate(value)
    } catch (e: Exception) {
        null
    }

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.parseOrNull(value: String) =
    try {
        validate(parseFn(value))
    } catch (e: Exception) {
        null
    }

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.ofListOrNull(vararg values: PRIMITIVE) =
    ofListOrNull(values.toList())

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.ofListOrNull(values: List<PRIMITIVE>) =
    values.orNull(::ofOrNull)

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.parseListOrNull(values: List<String>) =
    values.orNull(::parseOrNull)

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.parseListOrNull(vararg values: String) =
    parseListOrNull(values.toList())

private fun <IN, DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> List<IN>.orNull(fn: (IN) -> DOMAIN?) =
    when {
        isEmpty() -> emptyList()
        else -> mapNotNull(fn).takeIf(List<*>::isNotEmpty)
    }
