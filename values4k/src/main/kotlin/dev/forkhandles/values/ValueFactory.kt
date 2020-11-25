package dev.forkhandles.values

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.resultFrom

/**
 * Base value type for inline classes which enables type-safe primitives, along with Validation.
 */
abstract class ValueFactory<DOMAIN, PRIMITIVE>(
    internal val coerceFn: (PRIMITIVE) -> DOMAIN,
    private val validation: Validation<PRIMITIVE>? = null,
    internal val parseFn: (String) -> PRIMITIVE
) {
    internal fun validate(value: PRIMITIVE): DOMAIN {
        validation?.check(value)
        return coerceFn(value)
    }

    fun parse(value: String) = validate(parseFn(value))

    fun of(value: PRIMITIVE) = validate(value)
}

/**
 * Return a Object/null based on validation.
 */
fun <DOMAIN, PRIMITIVE> ValueFactory<DOMAIN, PRIMITIVE>.ofOrNull(value: PRIMITIVE): DOMAIN? = try {
    validate(value)
} catch (e: Exception) {
    null
}

/**
 * Parse a Object/null based on validation.
 */
fun <DOMAIN, PRIMITIVE> ValueFactory<DOMAIN, PRIMITIVE>.parseOrNull(value: String): DOMAIN? = try {
    validate(parseFn(value))
} catch (e: Exception) {
    null
}

/**
 * Return a Result4k Success/Failure based on validation.
 */
fun <DOMAIN, PRIMITIVE> ValueFactory<DOMAIN, PRIMITIVE>.ofResult4k(value: PRIMITIVE): Result<DOMAIN, Exception> = resultFrom { validate(value) }

/**
 * Return a Result4k Success/Failure based on validation.
 */
fun <DOMAIN, PRIMITIVE> ValueFactory<DOMAIN, PRIMITIVE>.parseResult4k(value: String): Result<DOMAIN, Exception> = resultFrom { validate(parseFn(value)) }
