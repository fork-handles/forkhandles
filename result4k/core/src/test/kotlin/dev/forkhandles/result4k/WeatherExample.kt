package dev.forkhandles.result4k

import java.math.BigDecimal

data class Weather(val kelvin: BigDecimal, val pascals: Int)
data class Conditions(val message: String)
data class WeatherError(val code: Int, val message: String)

private val cold = 283.15.toBigDecimal()
private val hot = 298.15.toBigDecimal()

fun getWeather(location: Int): Result<Weather, WeatherError> =
    when (location) {
        in 1..100 -> Success(Weather(kelvin = BigDecimal("295.15"), pascals = 101_390))
        else -> Failure(WeatherError(code = 404, message = "unsupported location"))
    }

fun Weather.toConditions(): Result<Conditions, WeatherError> =
    when {
        kelvin < BigDecimal.ZERO -> Failure(WeatherError(400, "impossible!"))
        kelvin < cold -> Success(Conditions("cold :("))
        kelvin > hot -> Success(Conditions("HOT! X("))
        else -> Success(Conditions("Nice :)"))
    }

/**
 * Get the current weather, interpret the conditions, and print them
 */
fun main() {
    val forecast: String = getWeather(20) // get initial result (success or failure)
        .flatMap(Weather::toConditions) // convert success to result (success or failure)
        .map { it.message } // convert success to success
        .mapFailure { message -> "WARNING: $message" } // convert failure to failure
        .peekFailure { println("Physics has imploded!") }  // perform side-effect if failure
        .get() // unwrap success, or failure if same type as success (in this case, String)

    println(forecast)
}
