package dev.forkhandles.result4k.hamkrest

import com.natpryce.hamkrest.and
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.greaterThan
import com.natpryce.hamkrest.has
import com.natpryce.hamkrest.isA
import com.natpryce.hamkrest.lessThan
import dev.forkhandles.result4k.Weather
import dev.forkhandles.result4k.WeatherError
import dev.forkhandles.result4k.getWeather
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class WeatherExampleHamkrest {
    @Test
    fun `assert any success`() = assertThat(getWeather(30), isSuccess())

    @Test
    fun `assert exact success`() = assertThat(
        getWeather(20),
        isSuccess(Weather(BigDecimal("295.15"), 101_390))
    )

    @Test
    fun `assert ranged success failure`() = assertThat(
        getWeather(20),
        isSuccess(has(Weather::pascals, greaterThan(100_000) and lessThan(200_000)))
    )

    @Test
    fun `assert any failure`() = assertThat(getWeather(9001), isFailure())

    @Test
    fun `assert exact failure`() = assertThat(
        getWeather(9001),
        isFailure(WeatherError(404, "unsupported location"))
    )

    @Test
    fun `assert ranged failure`() = assertThat(
        getWeather(9001),
        isFailure(isA<WeatherError>(has(WeatherError::code, greaterThan(400) and lessThan(500))))
    )
}
