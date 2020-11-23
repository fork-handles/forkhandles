package dev.forkhandles.values

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.OffsetDateTime
import java.time.OffsetTime
import java.time.YearMonth
import java.time.ZonedDateTime
import java.util.UUID

class FactoriesTest {

    @Test
    fun `primitive factories`() {
        1.also { assertThat(object : IntValueFactory<Int>({ it }) {}.parse(it.toString()), equalTo(it)) }
        1L.also { assertThat(object : LongValueFactory<Long>({ it }) {}.parse(it.toString()), equalTo(it)) }
        1.0.also { assertThat(object : DoubleValueFactory<Double>({ it }) {}.parse(it.toString()), equalTo(it)) }
        1.0f.also { assertThat(object : FloatValueFactory<Float>({ it }) {}.parse(it.toString()), equalTo(it)) }
        "1.0".also { assertThat(object : BigDecimalValueFactory<BigDecimal>({ it }) {}.parse(it), equalTo(BigDecimal(it))) }
        "1".also { assertThat(object : BigIntegerValueFactory<BigInteger>({ it }) {}.parse(it), equalTo(BigInteger(it))) }
        true.also { assertThat(object : BooleanValueFactory<Boolean>({ it }) {}.parse(it.toString()), equalTo(it)) }
        "hello".also { assertThat(object : StringValueFactory<String>({ it }) {}.parse(it), equalTo(it)) }
        UUID.randomUUID().also { assertThat(object : UUIDValueFactory<UUID>({ it }) {}.parse(it.toString()), equalTo(it)) }

        Instant.now().also { assertThat(object : InstantValueFactory<Instant>({ it }) {}.parse(it.toString()), equalTo(it)) }
        LocalTime.now().also { assertThat(object : LocalTimeValueFactory<LocalTime>({ it }) {}.parse(it.toString()), equalTo(it)) }
        LocalDate.now().also { assertThat(object : LocalDateValueFactory<LocalDate>({ it }) {}.parse(it.toString()), equalTo(it)) }
        LocalDateTime.now().also { assertThat(object : LocalDateTimeValueFactory<LocalDateTime>({ it }) {}.parse(it.toString()), equalTo(it)) }
        ZonedDateTime.now().also { assertThat(object : ZonedDateTimeValueFactory<ZonedDateTime>({ it }) {}.parse(it.toString()), equalTo(it)) }
        OffsetDateTime.now().also { assertThat(object : OffsetDateTimeValueFactory<OffsetDateTime>({ it }) {}.parse(it.toString()), equalTo(it)) }
        OffsetTime.now().also { assertThat(object : OffsetTimeValueFactory<OffsetTime>({ it }) {}.parse(it.toString()), equalTo(it)) }
        YearMonth.now().also { assertThat(object : YearMonthValueFactory<YearMonth>({ it }) {}.parse(it.toString()), equalTo(it)) }
    }
}
