package dev.forkhandles.result4k.strikt

import dev.forkhandles.result4k.Weather
import dev.forkhandles.result4k.WeatherError
import dev.forkhandles.result4k.getWeather
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import java.math.BigDecimal

class WeatherExampleKotest {
    @Test
    fun `assert any success`() {
        expectThat(getWeather(20)).isSuccess()
    }

    @Test
    fun `assert type success`() {
        expectThat(getWeather(20)).isSuccess<Weather>()
    }

    @Test
    fun `assert exact success`() {
        expectThat(getWeather(20)).isSuccess(Weather(BigDecimal("295.15"), 101_390))
    }

    @Test
    fun `assert any failure`() {
        expectThat(getWeather(9001)).isFailure()
    }

    @Test
    fun `assert type failure`() {
        expectThat(getWeather(9001)).isFailure<WeatherError>()
    }

    @Test
    fun `assert exact failure`() {
        expectThat(getWeather(9001)).isFailure(WeatherError(404, "unsupported location"))
    }
}
