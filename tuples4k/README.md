# Tuples4k

<a href="https://mvnrepository.com/artifact/dev.forkhandles"><img alt="Download" src="https://img.shields.io/maven-central/v/dev.forkhandles/forkhandles-bom"></a>
[![.github/workflows/build.yaml](https://github.com/fork-handles/forkhandles/actions/workflows/build.yaml/badge.svg)](https://github.com/fork-handles/forkhandles/actions/workflows/build.yaml)
<a href="https://codecov.io/gh/fork-handles/forkhandles"><img src="https://codecov.io/gh/fork-handles/forkhandles/branch/trunk/graph/badge.svg"/></a>
<a href="http//www.apache.org/licenses/LICENSE-2.0"><img alt="GitHub license" src="https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat"></a>
<a href="http://kotlinlang.org"><img alt="kotlin" src="https://img.shields.io/badge/kotlin-1.8-blue.svg"></a>
<a href="https://codebeat.co/projects/github-com-fork-handles-forkhandles-trunk"><img alt="codebeat badge" src="https://codebeat.co/badges/5b369ed4-af27-46f4-ad9c-a307d900617e"></a>

Tuples with up to (currently) eight elements, and convenient operations for working with them.

## Installation

In Gradle, install the ForkHandles BOM and then this module in the dependency block:

```kotlin
implementation(platform("dev.forkhandles:forkhandles-bom:X.Y.Z"))
implementation("dev.forkhandles:tuples4k")
```

Tuple2 and Tuple3 are typealiases for Pair and Triple respectively.  Other tuple types are data classes with elements named `val1`, `val2`, etc.

Tuples are constructed with the `tuple` function.

## Operations

`A + TupleN<X,Y,...> : TupleM<A,X,Y,...>` – add an element at the front of a tuple

`TupleN<A,B,...> + X : TupleM<A,B,...X>` – add an element at the end of a tuple

`TupleN<A,B,...> + TupleM<X,Y,...> : TupleP<A,B,...,X,Y,...>` – append two tuples

`TupleN<T,T,...>.toList(): List<T>` – Convert a tuple to a list

`TupleN<T1?,T2?,...>.allNonNull(): TupleN<T1,T2,...>?` – convert a tuple with nullable elements to a nullable tuple with non-nullable elements
