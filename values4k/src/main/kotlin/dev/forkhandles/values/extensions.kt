package dev.forkhandles.values

import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Clock
import java.time.Instant
import java.time.Instant.EPOCH
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.LocalTime.MIDNIGHT
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.ZoneId
import java.time.ZoneOffset.UTC
import java.time.ZonedDateTime
import java.util.UUID
import kotlin.random.Random

fun <DOMAIN : Value<Int>> IntValueFactory<DOMAIN>.random(random: Random = Random) = of(random.nextInt())
fun <DOMAIN : Value<Long>> LongValueFactory<DOMAIN>.random(random: Random = Random) = of(random.nextLong())
fun <DOMAIN : Value<Double>> DoubleValueFactory<DOMAIN>.random(random: Random = Random) = of(random.nextDouble())
fun <DOMAIN : Value<Float>> FloatValueFactory<DOMAIN>.random(random: Random = Random) = of(random.nextFloat())
fun <DOMAIN : Value<Boolean>> BooleanValueFactory<DOMAIN>.random(random: Random = Random) = of(random.nextBoolean())
fun <DOMAIN : Value<BigInteger>> BigIntegerValueFactory<DOMAIN>.random(random: Random = Random) = of(BigInteger.valueOf(random.nextLong()))
fun <DOMAIN : Value<BigDecimal>> BigDecimalValueFactory<DOMAIN>.random(random: Random = Random) = of(BigDecimal(random.nextDouble()))
fun <DOMAIN : Value<UUID>> UUIDValueFactory<DOMAIN>.random(random: Random = Random) = of(UUID(random.nextLong(), random.nextLong()))

val <DOMAIN : Value<Int>> IntValueFactory<DOMAIN>.ZERO get() = of(0)
val <DOMAIN : Value<Long>> LongValueFactory<DOMAIN>.ZERO get() = of(0)
val <DOMAIN : Value<Double>> DoubleValueFactory<DOMAIN>.ZERO get() = of(0.0)
val <DOMAIN : Value<Float>> FloatValueFactory<DOMAIN>.ZERO get() = of(0.0f)
val <DOMAIN : Value<BigInteger>> BigIntegerValueFactory<DOMAIN>.ZERO get() = of(BigInteger.ZERO)
val <DOMAIN : Value<BigDecimal>> BigDecimalValueFactory<DOMAIN>.ZERO get() = of(BigDecimal.ZERO)

val <DOMAIN : Value<LocalDate>> LocalDateValueFactory<DOMAIN>.EPOCH get() = of(LocalDate.EPOCH)
val <DOMAIN : Value<LocalDateTime>> LocalDateTimeValueFactory<DOMAIN>.EPOCH get() = of(LocalDateTime.ofEpochSecond(0, 0, UTC))
val <DOMAIN : Value<ZonedDateTime>> ZonedDateTimeValueFactory<DOMAIN>.EPOCH get() = of(ZonedDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC")))
val <DOMAIN : Value<OffsetDateTime>> OffsetDateTimeValueFactory<DOMAIN>.EPOCH get() = of(OffsetDateTime.ofInstant(Instant.EPOCH, ZoneId.of("UTC")))
val <DOMAIN : Value<Instant>> InstantValueFactory<DOMAIN>.EPOCH get() = of(Instant.EPOCH)

val <DOMAIN : Value<LocalDate>> LocalDateValueFactory<DOMAIN>.MAX get() = of(LocalDate.MAX)
val <DOMAIN : Value<LocalDateTime>> LocalDateTimeValueFactory<DOMAIN>.MAX get() = of(LocalDateTime.ofEpochSecond(0, 0, UTC))
val <DOMAIN : Value<ZonedDateTime>> ZonedDateTimeValueFactory<DOMAIN>.MAX get() = of(ZonedDateTime.ofInstant(Instant.MAX, ZoneId.of("UTC")))
val <DOMAIN : Value<OffsetDateTime>> OffsetDateTimeValueFactory<DOMAIN>.MAX get() = of(OffsetDateTime.ofInstant(Instant.MAX, ZoneId.of("UTC")))
val <DOMAIN : Value<Instant>> InstantValueFactory<DOMAIN>.MAX get() = of(Instant.MAX)

fun <DOMAIN : Value<LocalDate>> LocalDateValueFactory<DOMAIN>.now(clock: Clock = Clock.systemUTC()) = of(LocalDate.now(clock))
fun <DOMAIN : Value<LocalDateTime>> LocalDateTimeValueFactory<DOMAIN>.now(clock: Clock = Clock.systemUTC()) = of(LocalDateTime.now(clock))
fun <DOMAIN : Value<ZonedDateTime>> ZonedDateTimeValueFactory<DOMAIN>.now(clock: Clock = Clock.systemUTC()) = of(ZonedDateTime.now(clock))
fun <DOMAIN : Value<OffsetDateTime>> OffsetDateTimeValueFactory<DOMAIN>.now(clock: Clock = Clock.systemUTC()) = of(OffsetDateTime.now(clock))
fun <DOMAIN : Value<Instant>> InstantValueFactory<DOMAIN>.now(clock: Clock = Clock.systemUTC()) = of(Instant.now(clock))
fun <DOMAIN : Value<LocalTime>> LocalTimeValueFactory<DOMAIN>.now(clock: Clock = Clock.systemUTC()) = of(LocalTime.now(clock))
fun <DOMAIN : Value<OffsetTime>> OffsetTimeValueFactory<DOMAIN>.now(clock: Clock = Clock.systemUTC()) = of(OffsetTime.now(clock))

val <DOMAIN : Value<LocalTime>> LocalTimeValueFactory<DOMAIN>.MIDNGIHT get() = of(MIDNIGHT)
val <DOMAIN : Value<OffsetTime>> OffsetTimeValueFactory<DOMAIN>.MIDNGIHT get() = of(OffsetTime.ofInstant(EPOCH, ZoneId.of("UTC")))

val <DOMAIN : Value<File>> FileValueFactory<DOMAIN>.USER_HOME get() = of(File(System.getProperty("user.home")))
val <DOMAIN : Value<File>> FileValueFactory<DOMAIN>.ROOT get() = of(File("/"))
val <DOMAIN : Value<File>> FileValueFactory<DOMAIN>.WORKING_DIR get() = of(File("."))
