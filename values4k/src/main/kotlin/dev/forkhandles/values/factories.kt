package dev.forkhandles.values

import java.io.File
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
import java.time.format.DateTimeFormatter.ISO_INSTANT
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE
import java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME
import java.time.format.DateTimeFormatter.ISO_LOCAL_TIME
import java.time.format.DateTimeFormatter.ISO_OFFSET_DATE_TIME
import java.time.format.DateTimeFormatter.ISO_OFFSET_TIME
import java.time.format.DateTimeFormatter.ISO_ZONED_DATE_TIME
import java.time.format.DateTimeFormatter.ofPattern
import java.util.UUID

private val rfcBase64Alphabet get() = "^[0-9A-Za-z+/]+$".toRegex() // https://www.rfc-editor.org/rfc/rfc4648.html#section-4
private val rfcBase32Alphabet get() = "^[2-7A-Z]+$".toRegex()  // https://www.rfc-editor.org/rfc/rfc4648.html#section-6
private val rfcBase16Alphabet get() = "^[0-9A-F]+$".toRegex() // https://www.rfc-editor.org/rfc/rfc4648.html#section-8
private val base36Alphabet get() = "^[0-9A-Z]+$".toRegex()

open class StringValueFactory<DOMAIN : Value<String>>(
    fn: (String) -> DOMAIN, validation: Validation<String>? = null,
    showFn: (String) -> String = { it }
) : ValueFactory<DOMAIN, String>(fn, validation, { it }, showFn)

open class NonEmptyStringValueFactory<DOMAIN : Value<String>>(
    fn: (String) -> DOMAIN,
    showFn: (String) -> String = { it }
) : ValueFactory<DOMAIN, String>(fn, 1.minLength, { it }, showFn)

open class NonBlankStringValueFactory<DOMAIN : Value<String>>(
    fn: (String) -> DOMAIN,
    showFn: (String) -> String = { it }
) : ValueFactory<DOMAIN, String>(fn, 1.minLength.let { v -> { v(it.trim()) } }, { it }, showFn)

open class Base64StringValueFactory<DOMAIN : Value<String>>(
    fn: (String) -> DOMAIN,
    validation: Validation<String> = { true },
    parseFn: (String) -> String = { it },
    showFn: (String) -> String = { it },
) : ValueFactory<DOMAIN, String>(fn, rfcBase64Alphabet::matches.and(validation), parseFn, showFn)

open class Base36StringValueFactory<DOMAIN : Value<String>>(
    fn: (String) -> DOMAIN,
    validation: Validation<String> = { true },
    parseFn: (String) -> String = { it },
    showFn: (String) -> String = { it },
) : ValueFactory<DOMAIN, String>(fn, base36Alphabet::matches.and(validation), parseFn, showFn)

open class Base32StringValueFactory<DOMAIN : Value<String>>(
    fn: (String) -> DOMAIN,
    validation: Validation<String> = { true },
    parseFn: (String) -> String = { it },
    showFn: (String) -> String = { it },
) : ValueFactory<DOMAIN, String>(fn, rfcBase32Alphabet::matches.and(validation), parseFn, showFn)

open class Base16StringValueFactory<DOMAIN : Value<String>>(
    fn: (String) -> DOMAIN,
    validation: Validation<String> = { true },
    parseFn: (String) -> String = { it },
    showFn: (String) -> String = { it },
) : ValueFactory<DOMAIN, String>(fn, rfcBase16Alphabet::matches.and(validation), parseFn, showFn)

open class CharValueFactory<DOMAIN : Value<Char>>(
    fn: (Char) -> DOMAIN, validation: Validation<Char>? = null
) : ValueFactory<DOMAIN, Char>(fn, validation, String::first)

open class IntValueFactory<DOMAIN : Value<Int>>(
    fn: (Int) -> DOMAIN, validation: Validation<Int>? = null
) : ValueFactory<DOMAIN, Int>(fn, validation, String::toInt)

open class LongValueFactory<DOMAIN : Value<Long>>(
    fn: (Long) -> DOMAIN, validation: Validation<Long>? = null
) : ValueFactory<DOMAIN, Long>(fn, validation, String::toLong)

open class DoubleValueFactory<DOMAIN : Value<Double>>(
    fn: (Double) -> DOMAIN, validation: Validation<Double>? = null
) : ValueFactory<DOMAIN, Double>(fn, validation, String::toDouble)

open class FloatValueFactory<DOMAIN : Value<Float>>(
    fn: (Float) -> DOMAIN, validation: Validation<Float>? = null
) : ValueFactory<DOMAIN, Float>(fn, validation, String::toFloat)

open class BooleanValueFactory<DOMAIN : Value<Boolean>>(
    fn: (Boolean) -> DOMAIN, validation: Validation<Boolean>? = null
) : ValueFactory<DOMAIN, Boolean>(fn, validation, String::toBoolean)

open class BigIntegerValueFactory<DOMAIN : Value<BigInteger>>(
    fn: (BigInteger) -> DOMAIN, validation: Validation<BigInteger>? = null
) : ValueFactory<DOMAIN, BigInteger>(fn, validation, String::toBigInteger)

open class BigDecimalValueFactory<DOMAIN : Value<BigDecimal>>(
    fn: (BigDecimal) -> DOMAIN, validation: Validation<BigDecimal>? = null
) : ValueFactory<DOMAIN, BigDecimal>(fn, validation, String::toBigDecimal)

open class UUIDValueFactory<DOMAIN : Value<UUID>>(
    fn: (UUID) -> DOMAIN, validation: Validation<UUID>? = null
) : ValueFactory<DOMAIN, UUID>(fn, validation, UUID::fromString)

open class URLValueFactory<DOMAIN : Value<URL>>(
    fn: (URL) -> DOMAIN, validation: Validation<URL>? = null
) : ValueFactory<DOMAIN, URL>(fn, validation, ::URL)

open class DurationValueFactory<DOMAIN : Value<Duration>>(
    fn: (Duration) -> DOMAIN, validation: Validation<Duration>? = null
) : ValueFactory<DOMAIN, Duration>(fn, validation, { Duration.parse(it) })

open class InstantValueFactory<DOMAIN : Value<Instant>>(
    fn: (Instant) -> DOMAIN, validation: Validation<Instant>? = null,
    fmt: DateTimeFormatter = ISO_INSTANT
) : ValueFactory<DOMAIN, Instant>(fn, validation, { fmt.parse(it, Instant::from) }, fmt::format)

open class LocalDateValueFactory<DOMAIN : Value<LocalDate>>(
    fn: (LocalDate) -> DOMAIN, validation: Validation<LocalDate>? = null,
    fmt: DateTimeFormatter = ISO_LOCAL_DATE
) : ValueFactory<DOMAIN, LocalDate>(fn, validation, { LocalDate.parse(it, fmt) }, fmt::format)

open class LocalTimeValueFactory<DOMAIN : Value<LocalTime>>(
    fn: (LocalTime) -> DOMAIN,
    validation: Validation<LocalTime>? = null,
    fmt: DateTimeFormatter = ISO_LOCAL_TIME
) : ValueFactory<DOMAIN, LocalTime>(fn, validation, { LocalTime.parse(it, fmt) }, fmt::format)

open class LocalDateTimeValueFactory<DOMAIN : Value<LocalDateTime>>(
    fn: (LocalDateTime) -> DOMAIN,
    validation: Validation<LocalDateTime>? = null,
    fmt: DateTimeFormatter = ISO_LOCAL_DATE_TIME
) : ValueFactory<DOMAIN, LocalDateTime>(fn, validation, { LocalDateTime.parse(it, fmt) }, fmt::format)

open class OffsetDateTimeValueFactory<DOMAIN : Value<OffsetDateTime>>(
    fn: (OffsetDateTime) -> DOMAIN,
    validation: Validation<OffsetDateTime>? = null,
    fmt: DateTimeFormatter = ISO_OFFSET_DATE_TIME
) : ValueFactory<DOMAIN, OffsetDateTime>(fn, validation, { OffsetDateTime.parse(it, fmt) }, fmt::format)

open class OffsetTimeValueFactory<DOMAIN : Value<OffsetTime>>(
    fn: (OffsetTime) -> DOMAIN,
    validation: Validation<OffsetTime>? = null,
    fmt: DateTimeFormatter = ISO_OFFSET_TIME
) : ValueFactory<DOMAIN, OffsetTime>(fn, validation, { OffsetTime.parse(it, fmt) }, fmt::format)

open class PeriodValueFactory<DOMAIN : Value<Period>>(
    fn: (Period) -> DOMAIN,
    validation: Validation<Period>? = null
) : ValueFactory<DOMAIN, Period>(fn, validation, { Period.parse(it) })

open class YearMonthValueFactory<DOMAIN : Value<YearMonth>>(
    fn: (YearMonth) -> DOMAIN,
    validation: Validation<YearMonth>? = null,
    fmt: DateTimeFormatter = ofPattern("yyyy-MM")
) : ValueFactory<DOMAIN, YearMonth>(fn, validation, { YearMonth.parse(it, fmt) }, fmt::format)

open class YearValueFactory<DOMAIN : Value<Year>>(
    fn: (Year) -> DOMAIN,
    validation: Validation<Year>? = null,
    fmt: DateTimeFormatter = ofPattern("yyyy")
) : ValueFactory<DOMAIN, Year>(fn, validation, { Year.parse(it, fmt) }, fmt::format)

open class ZonedDateTimeValueFactory<DOMAIN : Value<ZonedDateTime>>(
    fn: (ZonedDateTime) -> DOMAIN,
    validation: Validation<ZonedDateTime>? = null,
    fmt: DateTimeFormatter = ISO_ZONED_DATE_TIME
) : ValueFactory<DOMAIN, ZonedDateTime>(fn, validation, { ZonedDateTime.parse(it, fmt) }, fmt::format)

open class FileValueFactory<DOMAIN : Value<File>>(
    fn: (File) -> DOMAIN, validation: Validation<File>? = null
) : ValueFactory<DOMAIN, File>(fn, validation, { File(it) })
