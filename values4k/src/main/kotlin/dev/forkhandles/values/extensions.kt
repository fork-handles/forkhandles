package dev.forkhandles.values

import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Clock
import java.time.Clock.systemUTC
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZoneId
import java.time.ZoneOffset.UTC
import java.time.ZonedDateTime
import java.time.temporal.Temporal
import java.util.UUID
import kotlin.random.Random

val <DOMAIN : Value<Int>> IntValueFactory<DOMAIN>.ZERO get() = of(0)
fun <DOMAIN : Value<Int>> IntValueFactory<DOMAIN>.random(random: Random = Random) = of(random.nextInt())
fun <DOMAIN : Value<Int>> IntValueFactory<DOMAIN>.plus(v1: DOMAIN, v2: DOMAIN): DOMAIN = of(v1.value + v2.value)
fun <DOMAIN : Value<Int>> IntValueFactory<DOMAIN>.minus(v1: DOMAIN, v2: DOMAIN): DOMAIN = of(v1.value - v2.value)
fun <DOMAIN : Value<Int>> IntValueFactory<DOMAIN>.times(v1: DOMAIN, v2: DOMAIN): DOMAIN = of(v1.value * v2.value)

val <DOMAIN : Value<Long>> LongValueFactory<DOMAIN>.ZERO get() = of(0)
fun <DOMAIN : Value<Long>> LongValueFactory<DOMAIN>.random(random: Random = Random) = of(random.nextLong())
fun <DOMAIN : Value<Long>> LongValueFactory<DOMAIN>.plus(v1: DOMAIN, v2: DOMAIN): DOMAIN = of(v1.value + v2.value)
fun <DOMAIN : Value<Long>> LongValueFactory<DOMAIN>.minus(v1: DOMAIN, v2: DOMAIN): DOMAIN = of(v1.value - v2.value)
fun <DOMAIN : Value<Long>> LongValueFactory<DOMAIN>.times(v1: DOMAIN, v2: DOMAIN): DOMAIN = of(v1.value * v2.value)

val <DOMAIN : Value<Double>> DoubleValueFactory<DOMAIN>.ZERO get() = of(0.0)
fun <DOMAIN : Value<Double>> DoubleValueFactory<DOMAIN>.random(random: Random = Random) = of(random.nextDouble())
fun <DOMAIN : Value<Double>> DoubleValueFactory<DOMAIN>.plus(v1: DOMAIN, v2: DOMAIN): DOMAIN = of(v1.value + v2.value)
fun <DOMAIN : Value<Double>> DoubleValueFactory<DOMAIN>.minus(v1: DOMAIN, v2: DOMAIN): DOMAIN = of(v1.value - v2.value)
fun <DOMAIN : Value<Double>> DoubleValueFactory<DOMAIN>.times(v1: DOMAIN, v2: DOMAIN): DOMAIN = of(v1.value * v2.value)

val <DOMAIN : Value<Float>> FloatValueFactory<DOMAIN>.ZERO get() = of(0.0f)
fun <DOMAIN : Value<Float>> FloatValueFactory<DOMAIN>.random(random: Random = Random) = of(random.nextFloat())
fun <DOMAIN : Value<Float>> FloatValueFactory<DOMAIN>.plus(v1: DOMAIN, v2: DOMAIN): DOMAIN = of(v1.value + v2.value)
fun <DOMAIN : Value<Float>> FloatValueFactory<DOMAIN>.minus(v1: DOMAIN, v2: DOMAIN): DOMAIN = of(v1.value - v2.value)
fun <DOMAIN : Value<Float>> FloatValueFactory<DOMAIN>.times(v1: DOMAIN, v2: DOMAIN): DOMAIN = of(v1.value * v2.value)

val <DOMAIN : Value<BigInteger>> BigIntegerValueFactory<DOMAIN>.ZERO get() = of(BigInteger.ZERO)
fun <DOMAIN : Value<BigInteger>> BigIntegerValueFactory<DOMAIN>.random(random: Random = Random) = of(BigInteger(random.nextLong().toString()))

fun <DOMAIN : Value<BigInteger>> BigIntegerValueFactory<DOMAIN>.plus(v1: DOMAIN, v2: DOMAIN) = of(v1.value + v2.value)
fun <DOMAIN : Value<BigInteger>> BigIntegerValueFactory<DOMAIN>.minus(v1: DOMAIN, v2: DOMAIN) = of(v1.value - v2.value)
fun <DOMAIN : Value<BigInteger>> BigIntegerValueFactory<DOMAIN>.times(v1: DOMAIN, v2: DOMAIN) = of(v1.value * v2.value)

val <DOMAIN : Value<BigDecimal>> BigDecimalValueFactory<DOMAIN>.ZERO get() = of(BigDecimal.ZERO)
fun <DOMAIN : Value<BigDecimal>> BigDecimalValueFactory<DOMAIN>.random(random: Random = Random) =
    of(BigDecimal(random.nextDouble()))

fun <DOMAIN : Value<BigDecimal>> BigDecimalValueFactory<DOMAIN>.plus(v1: DOMAIN, v2: DOMAIN) = of(v1.value + v2.value)
fun <DOMAIN : Value<BigDecimal>> BigDecimalValueFactory<DOMAIN>.minus(v1: DOMAIN, v2: DOMAIN) = of(v1.value - v2.value)
fun <DOMAIN : Value<BigDecimal>> BigDecimalValueFactory<DOMAIN>.times(v1: DOMAIN, v2: DOMAIN) = of(v1.value * v2.value)

fun <DOMAIN : Value<Boolean>> BooleanValueFactory<DOMAIN>.random(random: Random = Random) = of(random.nextBoolean())

val <DOMAIN : Value<UUID>> UUIDValueFactory<DOMAIN>.ZERO get() = of(0, 0)
fun <DOMAIN : Value<UUID>> UUIDValueFactory<DOMAIN>.of(first: Long = 0, second: Long = 0) = of(UUID(first, second))
fun <DOMAIN : Value<UUID>> UUIDValueFactory<DOMAIN>.random(random: Random = Random) = of(random.nextLong(), random.nextLong())

val <DOMAIN : Value<LocalDate>> LocalDateValueFactory<DOMAIN>.EPOCH get() = of(LocalDate.EPOCH)
val <DOMAIN : Value<LocalDate>> LocalDateValueFactory<DOMAIN>.MAX get() = of(LocalDate.MAX)
fun <DOMAIN : Value<LocalDate>> LocalDateValueFactory<DOMAIN>.now(clock: Clock = systemUTC()) =
    of(LocalDate.now(clock))

@JvmName("isBeforeLocalDate")
fun <DOMAIN : Value<LocalDate>> DOMAIN.isBefore(second: DOMAIN): Boolean = value < second.value
@JvmName("isAfterLocalDate")
fun <DOMAIN : Value<LocalDate>> DOMAIN.isAfter(second: DOMAIN): Boolean = value > second.value

val <DOMAIN : Value<LocalDateTime>> LocalDateTimeValueFactory<DOMAIN>.EPOCH
    get() = of(LocalDateTime.ofEpochSecond(0, 0, UTC))
val <DOMAIN : Value<LocalDateTime>> LocalDateTimeValueFactory<DOMAIN>.MAX
    get() = of(LocalDateTime.ofEpochSecond(0, 0, UTC))

fun <DOMAIN : Value<LocalDateTime>> LocalDateTimeValueFactory<DOMAIN>.now(clock: Clock = systemUTC()) =
    of(LocalDateTime.now(clock))

@JvmName("isBeforeLocalDateTime")
fun <DOMAIN : Value<LocalDateTime>> DOMAIN.isBefore(second: DOMAIN): Boolean = value < second.value
@JvmName("isAfterLocalDateTime")
fun <DOMAIN : Value<LocalDateTime>> DOMAIN.isAfter(second: DOMAIN): Boolean = value > second.value

val <DOMAIN : Value<ZonedDateTime>> ZonedDateTimeValueFactory<DOMAIN>.EPOCH
    get() = of(ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC")))
val <DOMAIN : Value<ZonedDateTime>> ZonedDateTimeValueFactory<DOMAIN>.MAX
    get() = of(ZonedDateTime.ofInstant(Instant.MAX, ZoneId.of("UTC")))

@JvmName("isBeforeZonedDateTime")
fun <DOMAIN : Value<ZonedDateTime>> ZonedDateTimeValueFactory<DOMAIN>.now(clock: Clock = systemUTC()) =
    of(ZonedDateTime.now(clock))

@JvmName("isAfterZonedDateTime")
fun <DOMAIN : Value<ZonedDateTime>> DOMAIN.isBefore(second: DOMAIN) = value < second.value
fun <DOMAIN : Value<ZonedDateTime>> DOMAIN.isAfter(second: DOMAIN) = value > second.value

val <DOMAIN : Value<OffsetDateTime>> OffsetDateTimeValueFactory<DOMAIN>.EPOCH
    get() = of(OffsetDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC")))

val <DOMAIN : Value<OffsetDateTime>> OffsetDateTimeValueFactory<DOMAIN>.MAX
    get() = of(OffsetDateTime.ofInstant(Instant.MAX, ZoneId.of("UTC")))

fun <DOMAIN : Value<OffsetDateTime>> OffsetDateTimeValueFactory<DOMAIN>.now(clock: Clock = systemUTC()) =
    of(OffsetDateTime.now(clock))

@JvmName("isBeforeOffsetDateTime")
fun <DOMAIN : Value<OffsetDateTime>> DOMAIN.isBefore(second: DOMAIN): Boolean = value < second.value
@JvmName("isAfterOffsetDateTime")
fun <DOMAIN : Value<OffsetDateTime>> DOMAIN.isAfter(second: DOMAIN): Boolean = value > second.value

val <DOMAIN : Value<Instant>> InstantValueFactory<DOMAIN>.EPOCH get() = of(Instant.EPOCH)
val <DOMAIN : Value<Instant>> InstantValueFactory<DOMAIN>.MAX get() = of(Instant.MAX)
fun <DOMAIN : Value<Instant>> InstantValueFactory<DOMAIN>.now(clock: Clock = systemUTC()) = of(Instant.now(clock))
@JvmName("isBeforeInstant")
fun <DOMAIN : Value<Instant>> DOMAIN.isBefore(second: DOMAIN): Boolean = value < second.value
@JvmName("isAfterInstant")
fun <DOMAIN : Value<Instant>> DOMAIN.isAfter(second: DOMAIN): Boolean = value > second.value

fun <DOMAIN : Temporal> Value<DOMAIN>.between(that: Value<DOMAIN>) = Duration.between(value, that.value)

fun <DOMAIN : Value<LocalTime>> LocalTimeValueFactory<DOMAIN>.now(clock: Clock = systemUTC()) = of(LocalTime.now(clock))

val <DOMAIN : Value<LocalTime>> LocalTimeValueFactory<DOMAIN>.MIDNIGHT get() = of(LocalTime.MIDNIGHT)
@JvmName("isBeforeLocalTime")
fun <DOMAIN : Value<LocalTime>> DOMAIN.isBefore(second: DOMAIN) = value < second.value
@JvmName("isAfterLocalTime")
fun <DOMAIN : Value<LocalTime>> DOMAIN.isAfter(second: DOMAIN) = value > second.value

fun <DOMAIN : Value<OffsetTime>> OffsetTimeValueFactory<DOMAIN>.now(clock: Clock = systemUTC()) =
    of(OffsetTime.now(clock))

@JvmName("isBeforeOffsetTime")
fun <DOMAIN : Value<OffsetTime>> DOMAIN.isBefore(second: DOMAIN) = value < second.value
@JvmName("isAfterOffsetTime")
fun <DOMAIN : Value<OffsetTime>> DOMAIN.isAfter(second: DOMAIN) = value > second.value

val <DOMAIN : Value<File>> FileValueFactory<DOMAIN>.USER_HOME get() = of(File(System.getProperty("user.home")))
val <DOMAIN : Value<File>> FileValueFactory<DOMAIN>.ROOT get() = of(File("/"))
val <DOMAIN : Value<File>> FileValueFactory<DOMAIN>.WORKING_DIR get() = of(File("."))

