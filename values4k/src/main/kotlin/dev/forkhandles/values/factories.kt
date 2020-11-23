package dev.forkhandles.values

import java.math.BigDecimal
import java.math.BigInteger
import java.net.URL
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.Period
import java.time.Year
import java.time.YearMonth
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.time.format.DateTimeFormatter.ISO_LOCAL_TIME
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
import java.time.format.DateTimeFormatter.ISO_OFFSET_TIME
import java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME
import java.util.UUID

abstract class StringValueFactory<DOMAIN> protected constructor(fn: (String) -> DOMAIN, validation: Validation<String>? = null)
    : ValueFactory<DOMAIN, String>(fn, validation, { it })

open class IntValueFactory<DOMAIN>(fn: (Int) -> DOMAIN, validation: Validation<Int>? = null)
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

abstract class UUIDValueFactory<DOMAIN> protected constructor(fn: (UUID) -> DOMAIN,
                                                              validation: Validation<UUID>? = null)
    : ValueFactory<DOMAIN, UUID>(fn, validation, UUID::fromString)

abstract class URLValueFactory<DOMAIN> protected constructor(fn: (URL) -> DOMAIN,
                                                             validation: Validation<URL>? = null)
    : ValueFactory<DOMAIN, URL>(fn, validation, ::URL)

abstract class DurationValueFactory<DOMAIN> protected constructor(fn: (Duration) -> DOMAIN,
                                                                  validation: Validation<Duration>? = null)
    : ValueFactory<DOMAIN, Duration>(fn, validation, { Duration.parse(it) })

abstract class InstantValueFactory<DOMAIN> protected constructor(fn: (Instant) -> DOMAIN,
                                                                 validation: Validation<Instant>? = null)
    : ValueFactory<DOMAIN, Instant>(fn, validation, Instant::parse)

abstract class LocalDateValueFactory<DOMAIN> protected constructor(fn: (LocalDate) -> DOMAIN,
                                                                   validation: Validation<LocalDate>? = null,
                                                                   formatter: DateTimeFormatter = ISO_LOCAL_DATE)
    : ValueFactory<DOMAIN, LocalDate>(fn, validation, { LocalDate.parse(it, formatter) })

abstract class LocalTimeValueFactory<DOMAIN> protected constructor(fn: (LocalTime) -> DOMAIN,
                                                                   validation: Validation<LocalTime>? = null,
                                                                   formatter: DateTimeFormatter = ISO_LOCAL_TIME)
    : ValueFactory<DOMAIN, LocalTime>(fn, validation, { LocalTime.parse(it, formatter) })


abstract class LocalDateTimeValueFactory<DOMAIN> protected constructor(fn: (LocalDateTime) -> DOMAIN,
                                                                       validation: Validation<LocalDateTime>? = null,
                                                                       formatter: DateTimeFormatter = ISO_LOCAL_DATE_TIME)
    : ValueFactory<DOMAIN, LocalDateTime>(fn, validation, { LocalDateTime.parse(it, formatter) })


abstract class OffsetDateTimeValueFactory<DOMAIN> protected constructor(fn: (OffsetDateTime) -> DOMAIN,
                                                                        validation: Validation<OffsetDateTime>? = null,
                                                                        formatter: DateTimeFormatter = ISO_OFFSET_DATE_TIME)
    : ValueFactory<DOMAIN, OffsetDateTime>(fn, validation, { OffsetDateTime.parse(it, formatter) })

abstract class OffsetTimeValueFactory<DOMAIN> protected constructor(fn: (OffsetTime) -> DOMAIN,
                                                                    validation: Validation<OffsetTime>? = null,
                                                                    formatter: DateTimeFormatter = ISO_OFFSET_TIME)
    : ValueFactory<DOMAIN, OffsetTime>(fn, validation, { OffsetTime.parse(it, formatter) })

abstract class PeriodValueFactory<DOMAIN> protected constructor(fn: (Period) -> DOMAIN,
                                                                validation: Validation<Period>? = null)
    : ValueFactory<DOMAIN, Period>(fn, validation, { Period.parse(it) })

abstract class YearMonthValueFactory<DOMAIN> protected constructor(fn: (YearMonth) -> DOMAIN,
                                                                   validation: Validation<YearMonth>? = null,
                                                                   formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM"))
    : ValueFactory<DOMAIN, YearMonth>(fn, validation, { YearMonth.parse(it, formatter) })

abstract class YearValueFactory<DOMAIN> protected constructor(fn: (Year) -> DOMAIN,
                                                              validation: Validation<Year>? = null,
                                                              formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy"))
    : ValueFactory<DOMAIN, Year>(fn, validation, { Year.parse(it, formatter) })


abstract class ZonedDateTimeValueFactory<DOMAIN> protected constructor(fn: (ZonedDateTime) -> DOMAIN,
                                                                       validation: Validation<ZonedDateTime>? = null,
                                                                       formatter: DateTimeFormatter = ISO_ZONED_DATE_TIME)
    : ValueFactory<DOMAIN, ZonedDateTime>(fn, validation, { ZonedDateTime.parse(it, formatter) })
