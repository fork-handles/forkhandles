package dev.forkhandles.values

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.resultFrom

/**
 * Base value type for inline classes which enables type-safe primitives, along with Validation.
 */
abstract class ValueFactory<DOMAIN, PRIMITIVE> protected constructor(val fn: (PRIMITIVE) -> DOMAIN, val validation: Validation<PRIMITIVE>? = null) {

    internal fun validate(value: PRIMITIVE): DOMAIN {
        validation?.check(value)
        return fn(value)
    }

    fun of(value: PRIMITIVE) = validate(value)
//
//    fun parse(value: String):  = parseFn(value)
}

//abstract class IntValueFactory() : ValueFactory<>
/**
 * Return a Object/null based on validation.
 */
fun <DOMAIN, PRIMITIVE> ValueFactory<DOMAIN, PRIMITIVE>.ofNullable(value: PRIMITIVE): DOMAIN? = try {
    validate(value)
} catch (e: IllegalArgumentException) {
    null
}

/**
 * Return a Result4k Success/Failure based on validation.
 */
fun <DOMAIN, PRIMITIVE> ValueFactory<DOMAIN, PRIMITIVE>.ofResult4k(value: PRIMITIVE): Result<DOMAIN, Exception> = resultFrom { validate(value) }
