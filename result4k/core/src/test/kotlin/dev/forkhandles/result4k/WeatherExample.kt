package dev.forkhandles.result4k

import java.math.BigDecimal

data class Weather(val kelvin: BigDecimal, val pascals: Int)
data class Conditions(val message: String)
data class WeatherError(val code: Int, val message: String)

private val cold = 283.15.toBigDecimal()
private val hot = 298.15.toBigDecimal()

fun getWeather(location: Int): Result<Weather, WeatherError> =
    begin
        .retainIf(location in (1..100), otherwise = {
            WeatherError(code = 404, message = "unsupported location")
        })
        .map { Weather(kelvin = BigDecimal("295.15"), pascals = 101_390) }

fun Weather.toConditions(): Result<Conditions, WeatherError> =
    when {
        kelvin < BigDecimal.ZERO -> WeatherError(400, "impossible!").asFailure()
        kelvin < cold -> Conditions("cold :(").asSuccess()
        kelvin > hot -> Conditions("HOT! X(").asSuccess()
        else -> Conditions("Nice :)").asSuccess()
    }

/**
 * Get the current weather, interpret the conditions, and print them
 */
fun main() {
    val forecast: String = getWeather(20) // get an initial result (success or failure)
        .flatMap(Weather::toConditions) // convert success to result (success or failure)
        .map { it.message } // convert success to message
        .peekFailure { println("Physics has imploded!") }  // perform side-effect if failure
        .recover { message -> "WARNING: $message" } // convert failure to message

    println(forecast)
}
