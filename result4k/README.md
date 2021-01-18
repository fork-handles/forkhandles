# Result4K

<a href="https://bintray.com/fork-handles/maven/forkhandles-bom/_latestVersion"><img alt="Download" src="https://api.bintray.com/packages/fork-handles/maven/forkhandles-bom/images/download.svg"></a>
<a href="https://travis-ci.org/fork-handles/forkhandles"><img alt="build status" src="https://travis-ci.org/fork-handles/forkhandles.svg?branch=trunk"/></a>
<a href="https://codecov.io/gh/fork-handles/forkhandles"><img src="https://codecov.io/gh/fork-handles/forkhandles/branch/trunk/graph/badge.svg"/></a>
<a href="http//www.apache.org/licenses/LICENSE-2.0"><img alt="GitHub license" src="https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat"></a>
<a href="http://kotlinlang.org"><img alt="kotlin" src="https://img.shields.io/badge/kotlin-1.4-blue.svg"></a>
<a href="https://codebeat.co/projects/github-com-fork-handles-forkhandles-trunk"><img alt="codebeat badge" src="https://codebeat.co/badges/5b369ed4-af27-46f4-ad9c-a307d900617e"></a>

Type safe error handling in Kotlin.

## Installation
In Gradle, install the BOM and then any other modules in the dependency block:

```kotlin
implementation(platform("dev.forkhandles:forkhandles-bom:X.Y.Z"))
implementation("dev.forkhandles:result4k")
```

## Motivation

Kotlin does not type-check exceptions.  Result4k lets you type-check code that reports and recovers from errors.

A `Result<T,E>` represents the result of a calculation of a _T_ value that might fail with an error of type _E_.

You can use a `when` expression to determine if a Result represents a success or a failure, but most of the time you don't need to.  Result4k type provides many useful operations for handling success or failure without explicit conditionals.

Result4k works with the grain of the Kotlin language. Kotlin does not have language support for monads (known as "do notation" or "for comprehensions" in other languages). A pure monadic approach becomes verbose and awkward.  Therefore, Result4k lets you use early returns to avoid deep nesting when propagating errors.
