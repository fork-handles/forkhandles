# fabrikate4k

<a href="https://mvnrepository.com/artifact/dev.forkhandles"><img alt="Download" src="https://img.shields.io/maven-central/v/dev.forkhandles/forkhandles-bom"></a>
[![.github/workflows/build.yaml](https://github.com/fork-handles/forkhandles/actions/workflows/build.yaml/badge.svg)](https://github.com/fork-handles/forkhandles/actions/workflows/build.yaml)
<a href="https://codecov.io/gh/fork-handles/forkhandles"><img src="https://codecov.io/gh/fork-handles/forkhandles/branch/trunk/graph/badge.svg"/></a>
<a href="http//www.apache.org/licenses/LICENSE-2.0"><img alt="GitHub license" src="https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat"></a>
<a href="http://kotlinlang.org"><img alt="kotlin" src="https://img.shields.io/badge/kotlin-1.4-blue.svg"></a>
<a href="https://codebeat.co/projects/github-com-fork-handles-forkhandles-trunk"><img alt="codebeat badge" src="https://codebeat.co/badges/5b369ed4-af27-46f4-ad9c-a307d900617e"></a>

A test utility to instantiate objects with fake data.

## Installation
In Gradle, install the ForkHandles BOM and then this module in the dependency block:

```kotlin
implementation(platform("dev.forkhandles:forkhandles-bom:X.Y.Z"))
testImplementation("dev.forkhandles:fabrikate4k")
```

## Usage

```kotlin
data class Hobby(val name: String)

data class Person(
    val firstName: String,
    val lastName: String,
    val age: Int,
    val hobbies: List<Hobby>,
    val birthdate: LocalDate
)

val randomPerson: Person = Fabrikate().random()

/*
Person(
    firstName=tzCNl, 
    lastName=SggkQ, 
    age=-191063271, 
    hobbies=[Hobby(name=q8IPqL)], 
    birthdate=1992-03-14
)
 */
```

If you need more control over the randomly created data you can register your own implementation:

```kotlin
val myRandomString: Fabricator<String> = UUID.randomUUID()::toString

val myRandomInt: Fabricator<Int> = { Random.nextInt(20, 60) }

val config = FabricatorConfig()
    .register(myRandomString)
    .register(myRandomInt)

val randomPerson: Person = Fabrikate(config).random()

/*
Person(
    firstName=acd9c9cc-646e-4890-b10d-03ada70b4ab7,
    lastName=7d31d77a-f9f8-4b6d-95df-8602a777e360, 
    age=39, 
    birthdate=2006-05-12,
    hobbies=[
        Hobby(name=7f2a55ec-ad0a-45a1-9a98-8b0d568f24ba), 
        Hobby(name=a34120ed-210e-4433-b2c1-5fa0692777f6), 
        Hobby(name=b132758e-cc2c-4df1-8479-bda6aae33e45), 
        Hobby(name=910813a3-3026-4e45-b5d6-253204e47eea), 
        Hobby(name=8b8f1b3e-e629-4603-8b93-2b8a1f3e1c4d)
    ]
)
 */
```

## Supported Types

* Int
* Long
* Double
* Float
* Char
* String
* ByteArray
* BigInteger
* BigDecimal
* Instant
* LocalDate
* LocalTime
* LocalDateTime
* OffsetTime
* OffsetDateTime
* ZonedDateTime
* Set
* List
* Map
* URI
* URL
* Date
* File
* UUID
* Duration

## Acknowledgement

The original code is copied
from [Creating a random instance of any class in Kotlin](https://blog.kotlin-academy.com/creating-a-random-instance-of-any-class-in-kotlin-b6168655b64a)
by [Marcin Moskala](http://marcinmoskala.com/).
