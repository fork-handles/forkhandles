package dev.forkhandles.result4k.hamkrest

import com.natpryce.hamkrest.assertion.assertThat
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
    fun `assert any failure`() = assertThat(getWeather(9001), isFailure())

    @Test
    fun `assert exact failure`() = assertThat(
        getWeather(9001),
        isFailure(WeatherError(404, "unsupported location"))
    )
}
