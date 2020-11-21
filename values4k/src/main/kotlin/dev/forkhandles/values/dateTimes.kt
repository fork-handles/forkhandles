@file:Suppress("unused")

package dev.forkhandles.values

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

typealias LocalTimeValue = Value<LocalTime>
typealias LocalDateValue = Value<LocalDate>
typealias LocalDateTimeValue = Value<LocalDateTime>
typealias OffsetTimeValue = Value<OffsetTime>
typealias OffsetDateTimeValue = Value<OffsetDateTime>
typealias ZonedDateTimeValue = Value<ZonedDateTime>
typealias InstantValue = Value<Instant>
typealias YearValue = Value<Year>
typealias YearMonthValue = Value<YearMonth>
typealias MonthValue = Value<Month>
typealias PeriodValue = Value<Period>
