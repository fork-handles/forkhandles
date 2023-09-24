package dev.forkhandles.result4k.kotest

import dev.forkhandles.result4k.Weather
import dev.forkhandles.result4k.WeatherError
import dev.forkhandles.result4k.getWeather
import io.kotest.matchers.ints.shouldBeGreaterThan
import io.kotest.matchers.ints.shouldBeInRange
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class WeatherExampleKotest {
    @Test
    fun `assert any success`() {
        getWeather(30).shouldBeSuccess()
    }

    @Test
    fun `assert exact success`() {
        getWeather(20) shouldBeSuccess Weather(BigDecimal("295.15"), 101_390)
    }

    @Test
    fun `assert success value`() {
        getWeather(10).shouldBeSuccess().pascals shouldBeGreaterThan 100_000
    }

    @Test
    fun `assert success block`() {
        getWeather(10) shouldBeSuccess { weather ->
            weather.pascals shouldBeGreaterThan 100_000
        }
    }

    @Test
    fun `assert any failure`() {
        getWeather(9001).shouldBeFailure()
    }

    @Test
    fun `assert exact failure`() {
        getWeather(9001) shouldBeFailure WeatherError(404, "unsupported location")
    }

    @Test
    fun `assert failure value`() {
        getWeather(9001).shouldBeFailure().code shouldBeInRange 400..499
    }

    @Test
    fun `assert failure block`() {
        getWeather(9001) shouldBeFailure { error ->
            error.code shouldBeInRange 400..499
        }
    }
}
