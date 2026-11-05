package dev.forkhandles.values

import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URI
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
import java.util.Base64
import java.util.UUID


private val rfcBase64Alphabet get() = "^[0-9A-Za-z+/=]+$".toRegex() // https://www.rfc-editor.org/rfc/rfc4648.html#section-4
private val rfcBase32Alphabet get() = "^[2-7A-Z=]+$".toRegex()  // https://www.rfc-editor.org/rfc/rfc4648.html#section-6
private val rfcBase16Alphabet get() = "^[0-9A-F]+$".toRegex() // https://www.rfc-editor.org/rfc/rfc4648.html#section-8
private val base36Alphabet get() = "^[0-9A-Z=]+$".toRegex()

open class StringValueFactory<DOMAIN : Value<String>>(
    fn: (String) -> DOMAIN, validation: Validation<String>? = null,
    showFn: (String) -> String = { it },
    onInvalid: ValueFactory<DOMAIN, String>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, String>.(String, Exception) -> Nothing = onInvalid,
) : ValueFactory<DOMAIN, String>(fn, validation, { it }, showFn, onInvalid, onParseFailure)

open class NonEmptyStringValueFactory<DOMAIN : Value<String>>(
    fn: (String) -> DOMAIN,
    showFn: (String) -> String = { it },
    onInvalid: ValueFactory<DOMAIN, String>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, String>.(String, Exception) -> Nothing = onInvalid,
) : ValueFactory<DOMAIN, String>(fn, 1.minLength, { it }, showFn, onInvalid, onParseFailure)

open class NonBlankStringValueFactory<DOMAIN : Value<String>>(
    fn: (String) -> DOMAIN,
    showFn: (String) -> String = { it },
    onInvalid: ValueFactory<DOMAIN, String>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, String>.(String, Exception) -> Nothing = onInvalid,
) : ValueFactory<DOMAIN, String>(fn, 1.minLength.let { v -> { v(it.trim()) } }, { it }, showFn, onInvalid, onParseFailure)

open class Base64StringValueFactory<DOMAIN : Value<String>>(
    fn: (String) -> DOMAIN,
    validation: Validation<String> = { true },
    parseFn: (String) -> String = { it },
    showFn: (String) -> String = { it },
    onInvalid: ValueFactory<DOMAIN, String>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, String>.(String, Exception) -> Nothing = onInvalid,
) : ValueFactory<DOMAIN, String>(fn, rfcBase64Alphabet::matches.and(validation), parseFn, showFn, onInvalid, onParseFailure) {
    private val encoder = Base64.getEncoder()
    fun encode(value: ByteArray) = encoder.encodeToString(value).let(coerceFn)
}

open class Base36StringValueFactory<DOMAIN : Value<String>>(
    fn: (String) -> DOMAIN,
    validation: Validation<String> = { true },
    parseFn: (String) -> String = { it },
    showFn: (String) -> String = { it },
    onInvalid: ValueFactory<DOMAIN, String>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, String>.(String, Exception) -> Nothing = onInvalid,
) : ValueFactory<DOMAIN, String>(fn, base36Alphabet::matches.and(validation), parseFn, showFn, onInvalid, onParseFailure)

open class Base32StringValueFactory<DOMAIN : Value<String>>(
    fn: (String) -> DOMAIN,
    validation: Validation<String> = { true },
    parseFn: (String) -> String = { it },
    showFn: (String) -> String = { it },
    onInvalid: ValueFactory<DOMAIN, String>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, String>.(String, Exception) -> Nothing = onInvalid,
) : ValueFactory<DOMAIN, String>(fn, rfcBase32Alphabet::matches.and(validation), parseFn, showFn, onInvalid, onParseFailure)

open class Base16StringValueFactory<DOMAIN : Value<String>>(
    fn: (String) -> DOMAIN,
    validation: Validation<String> = { true },
    parseFn: (String) -> String = { it },
    showFn: (String) -> String = { it },
    onInvalid: ValueFactory<DOMAIN, String>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, String>.(String, Exception) -> Nothing = onInvalid,
) : ValueFactory<DOMAIN, String>(fn, rfcBase16Alphabet::matches.and(validation), parseFn, showFn, onInvalid, onParseFailure) {
    // Source: https://stackoverflow.com/a/9855338/1253613
    private val base16Chars = "0123456789ABCDEF".toCharArray()
    fun encode(bytes: ByteArray): DOMAIN {
        val hexChars = CharArray(bytes.size * 2)
        for (j in bytes.indices) {
            val v = bytes[j].toInt() and 0xFF
            hexChars[j * 2] = this.base16Chars[v ushr 4]
            hexChars[j * 2 + 1] = this.base16Chars[v and 0x0F]
        }
        return String(hexChars).let(coerceFn)
    }
}

open class CharValueFactory<DOMAIN : Value<Char>>(
    fn: (Char) -> DOMAIN, validation: Validation<Char>? = null,
    onInvalid: ValueFactory<DOMAIN, Char>.(Char, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, Char>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
) : ValueFactory<DOMAIN, Char>(fn, validation, String::first, onInvalid = onInvalid, onParseFailure = onParseFailure)

open class IntValueFactory<DOMAIN : Value<Int>>(
    fn: (Int) -> DOMAIN, validation: Validation<Int>? = null,
    onInvalid: ValueFactory<DOMAIN, Int>.(Int, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, Int>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
) : ValueFactory<DOMAIN, Int>(fn, validation, String::toInt, onInvalid = onInvalid, onParseFailure = onParseFailure)

open class LongValueFactory<DOMAIN : Value<Long>>(
    fn: (Long) -> DOMAIN, validation: Validation<Long>? = null,
    onInvalid: ValueFactory<DOMAIN, Long>.(Long, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, Long>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
) : ValueFactory<DOMAIN, Long>(fn, validation, String::toLong, onInvalid = onInvalid, onParseFailure = onParseFailure)

open class DoubleValueFactory<DOMAIN : Value<Double>>(
    fn: (Double) -> DOMAIN, validation: Validation<Double>? = null,
    onInvalid: ValueFactory<DOMAIN, Double>.(Double, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, Double>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
) : ValueFactory<DOMAIN, Double>(fn, validation, String::toDouble, onInvalid = onInvalid, onParseFailure = onParseFailure)

open class FloatValueFactory<DOMAIN : Value<Float>>(
    fn: (Float) -> DOMAIN, validation: Validation<Float>? = null,
    onInvalid: ValueFactory<DOMAIN, Float>.(Float, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, Float>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
) : ValueFactory<DOMAIN, Float>(fn, validation, String::toFloat, onInvalid = onInvalid, onParseFailure = onParseFailure)

open class BooleanValueFactory<DOMAIN : Value<Boolean>>(
    fn: (Boolean) -> DOMAIN, validation: Validation<Boolean>? = null,
    onInvalid: ValueFactory<DOMAIN, Boolean>.(Boolean, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, Boolean>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
) : ValueFactory<DOMAIN, Boolean>(fn, validation, String::toBoolean, onInvalid = onInvalid, onParseFailure = onParseFailure)

open class BigIntegerValueFactory<DOMAIN : Value<BigInteger>>(
    fn: (BigInteger) -> DOMAIN, validation: Validation<BigInteger>? = null,
    onInvalid: ValueFactory<DOMAIN, BigInteger>.(BigInteger, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, BigInteger>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
) : ValueFactory<DOMAIN, BigInteger>(fn, validation, String::toBigInteger, onInvalid = onInvalid, onParseFailure = onParseFailure)

open class BigDecimalValueFactory<DOMAIN : Value<BigDecimal>>(
    fn: (BigDecimal) -> DOMAIN, validation: Validation<BigDecimal>? = null,
    onInvalid: ValueFactory<DOMAIN, BigDecimal>.(BigDecimal, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, BigDecimal>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
) : ValueFactory<DOMAIN, BigDecimal>(fn, validation, String::toBigDecimal, onInvalid = onInvalid, onParseFailure = onParseFailure)

open class UUIDValueFactory<DOMAIN : Value<UUID>>(
    fn: (UUID) -> DOMAIN, validation: Validation<UUID>? = null,
    onInvalid: ValueFactory<DOMAIN, UUID>.(UUID, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, UUID>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
) : ValueFactory<DOMAIN, UUID>(fn, validation, UUID::fromString, onInvalid = onInvalid, onParseFailure = onParseFailure)

open class URLValueFactory<DOMAIN : Value<URL>>(
    fn: (URL) -> DOMAIN, validation: Validation<URL>? = null,
    onInvalid: ValueFactory<DOMAIN, URL>.(URL, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, URL>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
) : ValueFactory<DOMAIN, URL>(fn, validation, { URI.create(it).toURL() }, onInvalid = onInvalid, onParseFailure = onParseFailure)

open class DurationValueFactory<DOMAIN : Value<Duration>>(
    fn: (Duration) -> DOMAIN, validation: Validation<Duration>? = null,
    onInvalid: ValueFactory<DOMAIN, Duration>.(Duration, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, Duration>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
) : ValueFactory<DOMAIN, Duration>(fn, validation, { Duration.parse(it) }, onInvalid = onInvalid, onParseFailure = onParseFailure)

open class InstantValueFactory<DOMAIN : Value<Instant>>(
    fn: (Instant) -> DOMAIN, validation: Validation<Instant>? = null,
    fmt: DateTimeFormatter = ISO_INSTANT,
    onInvalid: ValueFactory<DOMAIN, Instant>.(Instant, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, Instant>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
) : ValueFactory<DOMAIN, Instant>(fn, validation, { fmt.parse(it, Instant::from) }, fmt::format, onInvalid, onParseFailure)

open class LocalDateValueFactory<DOMAIN : Value<LocalDate>>(
    fn: (LocalDate) -> DOMAIN, validation: Validation<LocalDate>? = null,
    fmt: DateTimeFormatter = ISO_LOCAL_DATE,
    onInvalid: ValueFactory<DOMAIN, LocalDate>.(LocalDate, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, LocalDate>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
) : ValueFactory<DOMAIN, LocalDate>(fn, validation, { LocalDate.parse(it, fmt) }, fmt::format, onInvalid, onParseFailure)

open class LocalTimeValueFactory<DOMAIN : Value<LocalTime>>(
    fn: (LocalTime) -> DOMAIN,
    validation: Validation<LocalTime>? = null,
    fmt: DateTimeFormatter = ISO_LOCAL_TIME,
    onInvalid: ValueFactory<DOMAIN, LocalTime>.(LocalTime, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, LocalTime>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
) : ValueFactory<DOMAIN, LocalTime>(fn, validation, { LocalTime.parse(it, fmt) }, fmt::format, onInvalid, onParseFailure)

open class LocalDateTimeValueFactory<DOMAIN : Value<LocalDateTime>>(
    fn: (LocalDateTime) -> DOMAIN,
    validation: Validation<LocalDateTime>? = null,
    fmt: DateTimeFormatter = ISO_LOCAL_DATE_TIME,
    onInvalid: ValueFactory<DOMAIN, LocalDateTime>.(LocalDateTime, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, LocalDateTime>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
) : ValueFactory<DOMAIN, LocalDateTime>(fn, validation, { LocalDateTime.parse(it, fmt) }, fmt::format, onInvalid, onParseFailure)

open class OffsetDateTimeValueFactory<DOMAIN : Value<OffsetDateTime>>(
    fn: (OffsetDateTime) -> DOMAIN,
    validation: Validation<OffsetDateTime>? = null,
    fmt: DateTimeFormatter = ISO_OFFSET_DATE_TIME,
    onInvalid: ValueFactory<DOMAIN, OffsetDateTime>.(OffsetDateTime, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, OffsetDateTime>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
) : ValueFactory<DOMAIN, OffsetDateTime>(fn, validation, { OffsetDateTime.parse(it, fmt) }, fmt::format, onInvalid, onParseFailure)

open class OffsetTimeValueFactory<DOMAIN : Value<OffsetTime>>(
    fn: (OffsetTime) -> DOMAIN,
    validation: Validation<OffsetTime>? = null,
    fmt: DateTimeFormatter = ISO_OFFSET_TIME,
    onInvalid: ValueFactory<DOMAIN, OffsetTime>.(OffsetTime, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, OffsetTime>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
) : ValueFactory<DOMAIN, OffsetTime>(fn, validation, { OffsetTime.parse(it, fmt) }, fmt::format, onInvalid, onParseFailure)

open class PeriodValueFactory<DOMAIN : Value<Period>>(
    fn: (Period) -> DOMAIN,
    validation: Validation<Period>? = null,
    onInvalid: ValueFactory<DOMAIN, Period>.(Period, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, Period>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
) : ValueFactory<DOMAIN, Period>(fn, validation, { Period.parse(it) }, Period::toString, onInvalid, onParseFailure)

open class YearMonthValueFactory<DOMAIN : Value<YearMonth>>(
    fn: (YearMonth) -> DOMAIN,
    validation: Validation<YearMonth>? = null,
    fmt: DateTimeFormatter = ofPattern("yyyy-MM"),
    onInvalid: ValueFactory<DOMAIN, YearMonth>.(YearMonth, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, YearMonth>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
) : ValueFactory<DOMAIN, YearMonth>(fn, validation, { YearMonth.parse(it, fmt) }, fmt::format, onInvalid, onParseFailure)

open class YearValueFactory<DOMAIN : Value<Year>>(
    fn: (Year) -> DOMAIN,
    validation: Validation<Year>? = null,
    fmt: DateTimeFormatter = ofPattern("yyyy"),
    onInvalid: ValueFactory<DOMAIN, Year>.(Year, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, Year>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
) : ValueFactory<DOMAIN, Year>(fn, validation, { Year.parse(it, fmt) }, fmt::format, onInvalid, onParseFailure)

open class ZonedDateTimeValueFactory<DOMAIN : Value<ZonedDateTime>>(
    fn: (ZonedDateTime) -> DOMAIN,
    validation: Validation<ZonedDateTime>? = null,
    fmt: DateTimeFormatter = ISO_ZONED_DATE_TIME,
    onInvalid: ValueFactory<DOMAIN, ZonedDateTime>.(ZonedDateTime, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, ZonedDateTime>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
) : ValueFactory<DOMAIN, ZonedDateTime>(fn, validation, { ZonedDateTime.parse(it, fmt) }, fmt::format, onInvalid, onParseFailure)

open class FileValueFactory<DOMAIN : Value<File>>(
    fn: (File) -> DOMAIN, validation: Validation<File>? = null,
    onInvalid: ValueFactory<DOMAIN, File>.(File, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
    onParseFailure: ValueFactory<DOMAIN, File>.(String, Exception) -> Nothing = { _, e -> defaultOnInvalid(e) },
) : ValueFactory<DOMAIN, File>(fn, validation, { File(it) }, onInvalid = onInvalid, onParseFailure = onParseFailure)
