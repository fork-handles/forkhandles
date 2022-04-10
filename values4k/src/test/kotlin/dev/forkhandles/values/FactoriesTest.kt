package dev.forkhandles.values

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
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
import java.util.UUID
import kotlin.random.Random

class TV<T : Any>(value: T) : AbstractValue<T>(value)

class FactoriesTest {

    @Test
    fun `primitive factories`() {
        1.also { assertThat(IntValueFactory(::TV).parse(it.toString()), equalTo(TV(it))) }
        1L.also { assertThat(LongValueFactory(::TV).parse(it.toString()), equalTo(TV(it))) }
        1.0.also { assertThat(DoubleValueFactory(::TV).parse(it.toString()), equalTo(TV(it))) }
        1.0f.also { assertThat(FloatValueFactory(::TV).parse(it.toString()), equalTo(TV(it))) }
        "1.0".also { assertThat(BigDecimalValueFactory(::TV).parse(it), equalTo(TV(BigDecimal(it)))) }
        "1".also { assertThat(BigIntegerValueFactory(::TV).parse(it), equalTo(TV(BigInteger(it)))) }
        true.also { assertThat(BooleanValueFactory(::TV).parse(it.toString()), equalTo(TV(it))) }
        "hello".also { assertThat(StringValueFactory(::TV).parse(it), equalTo(TV(it))) }

        "hello".also { assertThat(NonEmptyStringValueFactory(::TV).parse(it), equalTo(TV(it))) }
        "".also { assertThat(NonEmptyStringValueFactory(::TV).parseOrNull(it), absent()) }

        "hello".also { assertThat(NonBlankStringValueFactory(::TV).parse(it), equalTo(TV(it))) }
        "   ".also { assertThat(NonBlankStringValueFactory(::TV).parseOrNull(it), absent()) }

        UUID.randomUUID().also { assertThat(UUIDValueFactory(::TV).parse(it.toString()), equalTo(TV(it))) }
        URL("http://localhost").also { assertThat(URLValueFactory(::TV).parse(it.toString()), equalTo(TV(it))) }

        Duration.ofHours(1).also { assertThat(DurationValueFactory(::TV).parse(it.toString()), equalTo(TV(it))) }
        Instant.now().also { assertThat(InstantValueFactory(::TV).parse(it.toString()), equalTo(TV(it))) }
        LocalDate.now().also { assertThat(LocalDateValueFactory(::TV).parse(it.toString()), equalTo(TV(it))) }
        LocalDateTime.now().also { assertThat(LocalDateTimeValueFactory(::TV).parse(it.toString()), equalTo(TV(it))) }
        LocalTime.now().also { assertThat(LocalTimeValueFactory(::TV).parse(it.toString()), equalTo(TV(it))) }
        OffsetDateTime.now().also { assertThat(OffsetDateTimeValueFactory(::TV).parse(it.toString()), equalTo(TV(it))) }
        OffsetTime.now().also { assertThat(OffsetTimeValueFactory(::TV).parse(it.toString()), equalTo(TV(it))) }
        Period.ofDays(1).also { assertThat(PeriodValueFactory(::TV).parse(it.toString()), equalTo(TV(it))) }
        Year.now().also { assertThat(YearValueFactory(::TV).parse(it.toString()), equalTo(TV(it))) }
        YearMonth.now().also { assertThat(YearMonthValueFactory(::TV).parse(it.toString()), equalTo(TV(it))) }
        ZonedDateTime.now().also { assertThat(ZonedDateTimeValueFactory(::TV).parse(it.toString()), equalTo(TV(it))) }
    }

    @Test
    fun extensions() {
        assertThat(IntValueFactory(::TV).random(Random(0)), equalTo(TV(-1934310868)))
        assertThat(LongValueFactory(::TV).random(Random(0)), equalTo(TV(-8307801916948173232)))
        assertThat(BooleanValueFactory(::TV).random(Random(0)), equalTo(TV(true)))
        assertThat(DoubleValueFactory(::TV).random(Random(0)), equalTo(TV(0.54963315022148)))
        assertThat(FloatValueFactory(::TV).random(Random(0)), equalTo(TV(0.54963315f)))
        assertThat(BigIntegerValueFactory(::TV).random(Random(0)), equalTo(TV(BigInteger.valueOf(-8307801916948173232))))
        assertThat(BigDecimalValueFactory(::TV).random(Random(0)), equalTo(TV(BigDecimal("0.5496331502214799602512584897340275347232818603515625"))))
        assertThat(UUIDValueFactory(::TV).random(Random(0)), equalTo(TV(UUID.fromString("8cb4c22c-53fe-ae50-d94e-97b2a94e6b1e"))))
    }
}

