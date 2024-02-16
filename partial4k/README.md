# Partial4k

<a href="https://mvnrepository.com/artifact/dev.forkhandles"><img alt="Download" src="https://img.shields.io/maven-central/v/dev.forkhandles/forkhandles-bom"></a>
[![.github/workflows/build.yaml](https://github.com/fork-handles/forkhandles/actions/workflows/build.yaml/badge.svg)](https://github.com/fork-handles/forkhandles/actions/workflows/build.yaml)

<a href="http//www.apache.org/licenses/LICENSE-2.0"><img alt="GitHub license" src="https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat"></a>
<a href="https://codebeat.co/projects/github-com-fork-handles-forkhandles-trunk"><img alt="codebeat badge" src="https://codebeat.co/badges/5b369ed4-af27-46f4-ad9c-a307d900617e"></a>

Partial application of functions.

## Installation

In Gradle, install the ForkHandles BOM and then this module in the dependency block:

```kotlin
implementation(platform("dev.forkhandles:forkhandles-bom:X.Y.Z"))
implementation("dev.forkhandles:partial4k")
```

## Usage

Import from `dev.forkhandles.partial`:

```kotlin

import dev.forkhandles.partial.partial
import dev.forkhandles.partial.`$1`
import dev.forkhandles.partial.`$2`
```

Use like this:

```kotlin
data class Footballer(val name: String, val dob: LocalDate, val locale: Locale)

// you can create a partially applied function by a call to `partial`
val english = ::Footballer.partial(`$2`, `$1`, Locale.UK)

// or, if you import `dev.forkhandles.partial.invoke`, by currying
val french = (::Footballer)(`$2`, `$1`, Locale.FRANCE)

// or you can apply one argument at a time..
val brazilian = (::Footballer)("Pelé")(LocalDate.of(1940, 10, 23))

// The placeholders (`$1`, `$2`, etc.) specify the order in which parameters must be
// passed to the partially applied function.  In this case, we have used them to
// switch the order of the first parameters while binding the value of the last parameter.

val davidBeckham = english(LocalDate.of(1975, 5, 2), "David Beckham")
val ericCantona = french(LocalDate.of(1966, 5, 24), "Eric Cantona")
val pele = brazilian(Locale.forLanguageTag("pt_BR"))

assertEquals(
    Footballer("David Beckham", LocalDate.of(1975, 5, 2), Locale.UK),
    davidBeckham
)
assertEquals(
    Footballer("Eric Cantona", LocalDate.of(1966, 5, 24), Locale.FRANCE),
    ericCantona
)
assertEquals(
    Footballer("Pelé", LocalDate.of(1940, 10, 23), Locale.forLanguageTag("pt_BR")),
    pele
)
```
