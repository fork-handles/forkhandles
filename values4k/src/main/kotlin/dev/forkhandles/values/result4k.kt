package dev.forkhandles.values

import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.map
import dev.forkhandles.result4k.resultFrom

/**
 * Return a Result4k Success/Failure based on validation.
 */

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.parseResult4k(value: String) =
    resultFrom { validate(parseFn(value)) }

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.ofListResult4k(values: List<PRIMITIVE>) =
    values.toResult4k(::ofResult4k)

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.ofListResult4k(vararg values: PRIMITIVE) =
    ofListResult4k(values.toList())

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.parseListResult4k(values: List<String>) =
    values.toResult4k(::parseResult4k)

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.parseListResult4k(vararg values: String) =
    parseListResult4k(values.toList())

fun <DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> ValueFactory<DOMAIN, PRIMITIVE>.ofResult4k(value: PRIMITIVE) =
    resultFrom { validate(value) }

private fun <IN, DOMAIN : Value<PRIMITIVE>, PRIMITIVE : Any> List<IN>.toResult4k(
    fn: (IN) -> Result<DOMAIN, Exception>
) = when {
    isEmpty() -> Success(emptyList())
    else ->
        drop(1)
            .fold(fn(first()).map(::listOf)) { acc, next ->
                when (acc) {
                    is Success -> fn(next).map { acc.value + it }
                    is Failure -> acc
                }
            }
}
