package dev.forkhandles.values

import java.math.BigDecimal
import java.math.BigInteger

abstract class StringValueFactory<DOMAIN> protected constructor(fn: (String) -> DOMAIN, validation: Validation<String>? = null)
    : ValueFactory<DOMAIN, String>(fn, validation, { it })

abstract class IntValueFactory<DOMAIN> protected constructor(fn: (Int) -> DOMAIN, validation: Validation<Int>? = null)
    : ValueFactory<DOMAIN, Int>(fn, validation, String::toInt)

abstract class LongValueFactory<DOMAIN> protected constructor(fn: (Long) -> DOMAIN, validation: Validation<Long>? = null)
    : ValueFactory<DOMAIN, Long>(fn, validation, String::toLong)

abstract class DoubleValueFactory<DOMAIN> protected constructor(fn: (Double) -> DOMAIN, validation: Validation<Double>? = null)
    : ValueFactory<DOMAIN, Double>(fn, validation, String::toDouble)

abstract class FloatValueFactory<DOMAIN> protected constructor(fn: (Float) -> DOMAIN, validation: Validation<Float>? = null)
    : ValueFactory<DOMAIN, Float>(fn, validation, String::toFloat)

abstract class BooleanValueFactory<DOMAIN> protected constructor(fn: (Boolean) -> DOMAIN, validation: Validation<Boolean>? = null)
    : ValueFactory<DOMAIN, Boolean>(fn, validation, String::toBoolean)

abstract class BigIntegerValueFactory<DOMAIN> protected constructor(fn: (BigInteger) -> DOMAIN, validation: Validation<BigInteger>? = null)
    : ValueFactory<DOMAIN, BigInteger>(fn, validation, String::toBigInteger)

abstract class BigDecimalValueFactory<DOMAIN> protected constructor(fn: (BigDecimal) -> DOMAIN, validation: Validation<BigDecimal>? = null)
    : ValueFactory<DOMAIN, BigDecimal>(fn, validation, String::toBigDecimal)
