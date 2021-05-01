package dev.forkhandles.values

/**
 * Return a Result Success/Failure based on validation.
 */

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.parseResult(value: String) =
    Result.runCatching { validate(parseFn(value)) }

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.ofListResult(values: List<PRIMITIVE>) =
    values.toResult(::ofResult)

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.ofListResult(vararg values: PRIMITIVE) =
    ofListResult(values.toList())

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.parseListResult(values: List<String>) =
    values.toResult(::parseResult)

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.parseListResult(vararg values: String) =
    parseListResult(values.toList())

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.ofResult(value: PRIMITIVE) =
    Result.runCatching { validate(value) }

private fun <IN, DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> List<IN>.toResult(
    fn: (IN) -> Result<DOMAIN>
) = when {
    isEmpty() -> Result.success(emptyList())
    else ->
        drop(1)
            .fold(fn(first()).map(::listOf)) { acc, next ->
                when {
                    acc.isSuccess -> fn(next).map { acc.getOrThrow() + it }
                    else -> acc
                }
            }
}
