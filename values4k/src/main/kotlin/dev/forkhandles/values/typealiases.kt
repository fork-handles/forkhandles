@file:Suppress("unused")

package dev.forkhandles.values

import java.math.BigDecimal
import java.math.BigInteger
import java.time.Duration
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.Month
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.Period
import java.time.Year
import java.time.YearMonth
import java.time.ZonedDateTime
import java.util.UUID

typealias StringValue = Value<String>
typealias IntValue = Value<Int>
typealias LongValue = Value<Long>
typealias DoubleValue = Value<Double>
typealias FloatValue = Value<Float>
typealias UUIDValue = Value<UUID>
typealias BigDecimalValue = Value<BigDecimal>
typealias BigIntegerValue = Value<BigInteger>
typealias LocalTimeValue = Value<LocalTime>
typealias LocalDateValue = Value<LocalDate>
typealias LocalDateTimeValue = Value<LocalDateTime>
typealias OffsetTimeValue = Value<OffsetTime>
typealias OffsetDateTimeValue = Value<OffsetDateTime>
typealias ZonedDateTimeValue = Value<ZonedDateTime>
typealias InstantValue = Value<Instant>
typealias YearMonthValue = Value<YearMonth>
typealias YearValue = Value<Year>
typealias MonthValue = Value<Month>
typealias DurationValue = Value<Duration>
typealias PeriodValue = Value<Period>
