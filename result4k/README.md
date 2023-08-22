# Result4K

<a href="https://mvnrepository.com/artifact/dev.forkhandles"><img alt="Download" src="https://img.shields.io/maven-central/v/dev.forkhandles/forkhandles-bom"></a>
[![.github/workflows/build.yaml](https://github.com/fork-handles/forkhandles/actions/workflows/build.yaml/badge.svg)](https://github.com/fork-handles/forkhandles/actions/workflows/build.yaml)
<a href="https://codecov.io/gh/fork-handles/forkhandles"><img src="https://codecov.io/gh/fork-handles/forkhandles/branch/trunk/graph/badge.svg"/></a>
<a href="http//www.apache.org/licenses/LICENSE-2.0"><img alt="GitHub license" src="https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat"></a>
<a href="https://codebeat.co/projects/github-com-fork-handles-forkhandles-trunk"><img alt="codebeat badge" src="https://codebeat.co/badges/5b369ed4-af27-46f4-ad9c-a307d900617e"></a>

Type safe error handling in Kotlin.

## Installation
In Gradle, install the ForkHandles BOM and then this module in the dependency block:

```kotlin
implementation(platform("dev.forkhandles:forkhandles-bom:X.Y.Z"))
implementation("dev.forkhandles:result4k")
```

## Motivation

Kotlin does not type-check exceptions.  Result4k lets you type-check code that reports and recovers from errors.

A `Result<T,E>` represents the result of a calculation of a _T_ value that might fail with an error of type _E_.

You can use a `when` expression to determine if a Result represents a success or a failure, but most of the time you don't need to.  Result4k type provides many useful operations for handling success or failure without explicit conditionals.

Result4k works with the grain of the Kotlin language. Kotlin does not have language support for monads (known as "do notation" or "for comprehensions" in other languages). A pure monadic approach becomes verbose and awkward.  Therefore, Result4k lets you use early returns to avoid deep nesting when propagating errors.

## Documentation

We really need some - but everyone is so busy. If you'd like to write a blog post send a PR and we'll reference it here.

In the meantime there is a [YouTube playlist](https://youtube.com/playlist?list=PL1ssMPpyqochiZj41oLAtvht4ScUurHJH) that demonstrates how to refactor from Kotlin exceptions to Result4k, or you can read Chapter 19 of the excellent (ahem) book [Java to Kotlin - A Refactoring Guidebook](https://java-to-kotlin.dev/).

## Example

```kotlin
data class Weather(val kelvin: BigDecimal, val pascals: Int)
data class Conditions(val message: String)
data class WeatherError(val code: Int, val message: String)

private val cold = 283.15.toBigDecimal()
private val hot = 298.15.toBigDecimal()

fun getWeather(location: Int): Result<Weather, WeatherError> = when(location) {
    in 1..100 -> Success(Weather(kelvin = BigDecimal("295.15"), pascals = 101_390))
    else -> Failure(WeatherError(code = 404, message = "unsupported location"))
}

fun Weather.toConditions(): Result<Conditions, WeatherError> {
     return when {
         kelvin < BigDecimal.ZERO -> Failure(WeatherError(400, "impossible!"))
         kelvin < cold -> Success(Conditions("cold :("))
         kelvin > hot -> Success(Conditions("HOT! X("))
         else -> Success(Conditions("Nice :)"))
     }
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
```

There is also an additional [PetStoreExample](core/src/test/kotlin/dev/forkhandles/result4k/petStoreExample.kt).

## Testing

There are built-in assertions for Kotest and Hamkrest.

### Kotest

```kotlin
implementation(platform("dev.forkhandles:forkhandles-bom:X.Y.Z"))
implementation("dev.forkhandles:result4k-kotest")
```

```kotlin
class WeatherExampleKotest {
    @Test
    fun `assert any success`() = getWeather(30).shouldBeSuccess()

    @Test
    fun `assert exact success`() = getWeather(20) shouldBeSuccess Weather(BigDecimal("295.15"), 101_390)

    @Test
    fun `assert success block`() = getWeather(10) shouldBeSuccess { weather ->
        weather.pascals shouldBeGreaterThan 100_000
    }

    @Test
    fun `assert any failure`() = getWeather(9001).shouldBeFailure()

    @Test
    fun `assert exact failure`() = getWeather(9001) shouldBeFailure WeatherError(404, "unsupported location")

    @Test
    fun `assert failure block`() = getWeather(9001) shouldBeFailure { error ->
        error.code shouldBeInRange 400..499
    }
}
```

### Hamkrest

```kotlin
implementation(platform("dev.forkhandles:forkhandles-bom:X.Y.Z"))
implementation("dev.forkhandles:result4k-hamkrest")
```

```kotlin
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
```
