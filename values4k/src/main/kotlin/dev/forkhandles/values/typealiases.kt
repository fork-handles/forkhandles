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

typealias StringValue = AbstractValue<String>
typealias IntValue = AbstractValue<Int>
typealias LongValue = AbstractValue<Long>
typealias DoubleValue = AbstractValue<Double>
typealias FloatValue = AbstractValue<Float>
typealias UUIDValue = AbstractValue<UUID>
typealias BigDecimalValue = AbstractValue<BigDecimal>
typealias BigIntegerValue = AbstractValue<BigInteger>
typealias InstantValue = AbstractValue<Instant>
typealias LocalTimeValue = AbstractValue<LocalTime>
typealias LocalDateValue = AbstractValue<LocalDate>
typealias LocalDateTimeValue = AbstractValue<LocalDateTime>
typealias OffsetTimeValue = AbstractValue<OffsetTime>
typealias OffsetDateTimeValue = AbstractValue<OffsetDateTime>
typealias ZonedDateTimeValue = AbstractValue<ZonedDateTime>
typealias YearMonthValue = AbstractValue<YearMonth>
typealias YearValue = AbstractValue<Year>
typealias MonthValue = AbstractValue<Month>
typealias DurationValue = AbstractValue<Duration>
typealias PeriodValue = AbstractValue<Period>
