# fabrikate4k

<a href="https://mvnrepository.com/artifact/dev.forkhandles"><img alt="Download" src="https://img.shields.io/maven-central/v/dev.forkhandles/forkhandles-bom"></a>
[![.github/workflows/build.yaml](https://github.com/fork-handles/forkhandles/actions/workflows/build.yaml/badge.svg)](https://github.com/fork-handles/forkhandles/actions/workflows/build.yaml)
<a href="https://codecov.io/gh/fork-handles/forkhandles"><img src="https://codecov.io/gh/fork-handles/forkhandles/branch/trunk/graph/badge.svg"/></a>
<a href="http//www.apache.org/licenses/LICENSE-2.0"><img alt="GitHub license" src="https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat"></a>

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

### Creating a custom `Fabrikator`

If you need more control over the randomly created data you can register your own implementation:

```kotlin
val myRandomString: Fabricator<String> = Fabricator { UUID.randomUUID().toString() }

val myRandomInt: Fabricator<Int> = Fabricator { Random.nextInt(20, 60) }

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

A fabricator can use the instance of `Fabrikate` if needed :

```kotlin
interface Person {
    val name: String
}

data class Parent(
    override val name: String,
    val children: List<Person>,
) : Person

data class Child(
    override val name: String,
) : Person

val myRandomParent: Fabricator<Parent> = Fabricator {
    Parent(it.random(), it.random<List<Child>>())
}

val myRandomPerson: Fabricator<Person> = Fabricator {
    if (!it.config.random.nextBoolean()) it.random<Parent>()
    else it.random<Child>()
}

val config = FabricatorConfig()
    .register(StringFabricator())
    .register(myRandomParent)
    .register(myRandomPerson)

val randomPerson: Person = Fabrikate(config).random()

/*
Parent(
    name=DAel,
    children=[
        Child(name=P56I7)
    ]
)
*/
```

### Configuring the fabrication

`FabrikatorConfig` can be configured by passing parameters to adjust the
behavior during fabrication.

| Parameter          | Description                                                                                                                                                                                                                                   |
|--------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `seed`             | An `Int` used as a seed to the random number generator. This is useful if you want to control creation, e.g. in tests.                                                                                                                        |
| `nullableStrategy` | A `NullableStrategy` that will prevent the creation of `null` values for nullable types when set to `NeverSetToNull`, or always creates `null` values for nullable types when set to `AlwaysSetToNull`.<br/> Defaults to `RandomlySetToNull`. |
| `collectionSizes`  | An `IntRange` that defines the number of elements to create when fabricating collections. Defaults to `1..5`.                                                                                                                                 |  

If you want to call a fabricator as a standalone :
```kotlin
val myRandomString: Fabricator<String> = Fabricator { UUID.randomUUID().toString() }

myRandomString()
```

You can also configure it :
```kotlin
val myRandomString: Fabricator<String> = Fabricator { UUID.randomUUID().toString() }

val randomPerson = Fabrikate().random<Person> { config ->
    config.withMapping(myRandomString)
}
```

It is possible to use `withMapping` like this as a shorthand :
```kotlin
val randomPerson = Fabrikate().random<Person> { config ->
    config.withMapping<String> { UUID.randomUUID().toString() }
}
```

Finally, you can create `Fabrikate` with a builder-like syntax :
```kotlin
val fabrikate = Fabrikate
    .withStandardMappings()
    .withMapping(myRandomPerson)
```

### Pitfalls

#### Standard mappings

When a `FabrikatorConfig` is instantiated, none of the default mappings
will be applied.
If you want to make use of those, make sure to
call `.withStandardMappings()`.

#### Registration ordering

The `register()` method of `FabrikatorConfig` will override the already
existing `Fabrikator` for that type.
If you need to provide a custom `Fabrikator` for one of supported types
and want to use the standard mappings also, make sure to
call `.register()` for your custom `Fabrikator` _after_
calling `.withStandardMappings()`.

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
* Date
* Year
* Month
* YearMonth
* Set
* List
* Map
* URI
* URL
* File
* UUID
* Duration

## Acknowledgement

The original code is copied
from [Creating a random instance of any class in Kotlin](https://blog.kotlin-academy.com/creating-a-random-instance-of-any-class-in-kotlin-b6168655b64a)
by [Marcin Moskala](http://marcinmoskala.com/).
