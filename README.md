# ForK Handles

<a href="https://github.com/fork-handles/forkhandles/actions?query=workflow%3A.github%2Fworkflows%2Fbuild.yaml"><img alt="build" src="https://github.com/fork-handles/forkhandles/workflows/.github/workflows/build.yaml/badge.svg"></a>
<a href="https://mvnrepository.com/artifact/dev.forkhandles"><img alt="download" src="https://img.shields.io/maven-central/v/dev.forkhandles/forkhandles-bom"></a>
<img alt="jcenter free" src="https://img.shields.io/badge/JCenter%20free-%3E1.8.4.1-success">

<a href="https://codecov.io/gh/fork-handles/forkhandles"><img src="https://codecov.io/gh/fork-handles/forkhandles/branch/trunk/graph/badge.svg"/></a>
<a href="http//www.apache.org/licenses/LICENSE-2.0"><img alt="GitHub license" src="https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat"></a>
<a href="https://codebeat.co/projects/github-com-fork-handles-forkhandles-trunk"><img alt="codebeat badge" src="https://codebeat.co/badges/5b369ed4-af27-46f4-ad9c-a307d900617e"></a>

Forkhandles (4k) contains foundational libraries for Kotlin
- [Bunting4k](bunting4k) - Command line argument parser
- [Fabrikate4k](fabrikate4k) - Test utility to instantiate objects with fake data
- [Parser4k](parser4k) - Recursive descent parser combinator library
- [Partial4k](partial4k) - Adds partial application of functions to Kotlin
- [Result4k](result4k) - A usable Result type
- [Time4k](time4k) - Clock and deterministic scheduler
- [Tuples4k](tuples4k) - Tuple classes
- [Values4k](values4k) - Value classes aka Microtypes aka Tinytypes

## Installation
In Gradle, install the BOM and then any other modules in the dependency block: 

```kotlin
implementation(platform("dev.forkhandles:forkhandles-bom:X.Y.Z"))
implementation("dev.forkhandles:$libraryName")
```
