# Data4k

<a href="https://mvnrepository.com/artifact/dev.forkhandles"><img alt="Download" src="https://img.shields.io/maven-central/v/dev.forkhandles/forkhandles-bom"></a>
[![.github/workflows/build.yaml](https://github.com/fork-handles/forkhandles/actions/workflows/build.yaml/badge.svg)](https://github.com/fork-handles/forkhandles/actions/workflows/build.yaml)
<a href="https://codecov.io/gh/fork-handles/forkhandles"><img src="https://codecov.io/gh/fork-handles/forkhandles/branch/trunk/graph/badge.svg"/></a>
<a href="http//www.apache.org/licenses/LICENSE-2.0"><img alt="GitHub license" src="https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat"></a>
<a href="https://codebeat.co/projects/github-com-fork-handles-forkhandles-trunk"><img alt="codebeat badge" src="https://codebeat.co/badges/5b369ed4-af27-46f4-ad9c-a307d900617e"></a>

Library to make working with Data-Oriented programming in Kotlin easier, to extract typed values from dynamic data structures such as Maps.

## Installation

In Gradle, install the ForkHandles BOM and then this module in the dependency block:

```kotlin
implementation(platform("dev.forkhandles:forkhandles-bom:X.Y.Z"))
implementation("dev.forkhandles:data4k")
```

## Usage 

The library defines a `DataContainer` class and implementations for:
- Map<String, Any?>
- Jackson JSON Node

Support for extracting:
- primitive values
- subobjects
- lists
- values4k value types

To extract data from the underlying data, define wrappers which provides access via delegated-properties:

```kotlin
class MapBacked(propertySet: Map<String, Any?>) : MapDataContainer(propertySet) {
    val stringField by field<String>()
    val listSubClassField by list(::SubMap)
    val objectField by obj(::SubMap)
}

class SubMap(propertySet: Map<String, Any?>) : MapDataContainer(propertySet) {
     val stringField by field<String>()
}

val input = MapBacked(
    mapOf(
        "stringField" to "string",
        "listSubClassField" to listOf(
            mapOf("stringField" to "string1"),
            mapOf("stringField" to "string2"),
        ),
        "listStringsField" to listOf("string1", "string2"),
        "objectField" to mapOf(
            "stringField" to "string"
        )
    )
)

// then just get the values from the underlying data using the type system. Errors will be thrown for missing/invalid properties
val data: String = input.objectField.stringField
```
