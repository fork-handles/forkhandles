# Data4k

<a href="https://mvnrepository.com/artifact/dev.forkhandles"><img alt="Download" src="https://img.shields.io/maven-central/v/dev.forkhandles/forkhandles-bom"></a>
[![.github/workflows/build.yaml](https://github.com/fork-handles/forkhandles/actions/workflows/build.yaml/badge.svg)](https://github.com/fork-handles/forkhandles/actions/workflows/build.yaml)

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

Support for extracting and writing both required and optional fields of types:
- primitive values
- sub-objects
- lists
- [values4k](https://github.com/fork-handles/forkhandles/tree/trunk/values4k) value types

To extract data from the underlying data, define wrappers which provides access via delegated-properties:

```kotlin

// our main top-level container
class MapBacked(propertySet: Map<String, Any?>) : MapDataContainer(propertySet) {
    val stringField: String by required<String>()
    val optionalList: List<String>? by optionalList<String>()
    val optionalStringField: String? by optional<String>()
    val listSubClassField: List<SubMap> by requiredList(::SubMap)
    val objectField: SubMap by requiredObj(::SubMap) 
    val valueField: MyInt by required(MyInt) // values4k Int tiny/micro type
}

// a sub-object
class SubMap(propertySet: Map<String, Any?>) : MapDataContainer(propertySet) {
     val stringField: String by required<String>()
     var optionalStringField: String? by optional<String>() // we can write to this field!
}

class MyInt private constructor(value: Int) : IntValue(value) {
    companion object : IntValueFactory<MyInt>(::MyInt)
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

// to write into the structure, just assign to a var
input.objectField.optionalStringField = "hello"
```


## Adding metadata to properties

data4k includes the ability to attach metadata to each property, which can be used for later retrieval from the instance of the container. To use:

```kotlin
// a custom type of metadatum
data class TagMetadatum(val tag: String, val value: String) : Metadatum

class MetaDataMapContainer(propertySet: Map<String, Any?>) : MapDataContainer(propertySet) {
    // create fields as usual, but attach the metadata in the declaration
    val stringField: String by required<String>(TagMetadatum("tag", "tagValue"))
}

val container = MetadataMapContainer(mapOf())

// get all metadata for fields
val metadata: List<PropertyMetadata> = container.propertyMetadata()

// each field can have multiple pieces of metadata and also contains the name and the KType of the field
val metadatum: List<Metadatum> = metadata.data

// get some data from the list..
val tagValue = (metadatum.first() as TagMetadatum).value
```
