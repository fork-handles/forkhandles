# ForK Handles

<a href="https://bintray.com/fork-handles/maven/forkhandles-bom/_latestVersion"><img alt="Download" src="https://api.bintray.com/packages/fork-handles/maven/forkhandles-bom/images/download.svg"></a>
<a href="https://travis-ci.org/fork-handles/forkhandles"><img alt="build status" src="https://travis-ci.org/fork-handles/forkhandles.svg?branch=trunk"/></a>
<a href="https://codecov.io/gh/fork-handles/forkhandles"><img src="https://codecov.io/gh/fork-handles/forkhandles/branch/trunk/graph/badge.svg"/></a>
<a href="http//www.apache.org/licenses/LICENSE-2.0"><img alt="GitHub license" src="https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat"></a>
<a href="http://kotlinlang.org"><img alt="kotlin" src="https://img.shields.io/badge/kotlin-1.4-blue.svg"></a>
<a href="https://codebeat.co/projects/github-com-fork-handles-forkhandles-trunk"><img alt="codebeat badge" src="https://codebeat.co/badges/5b369ed4-af27-46f4-ad9c-a307d900617e"></a>

Forkhandles (4k) contains foundational libraries for Kotlin
- [Bunting4k](bunting4k) - Command line argument parser
- [Parser4k](parser4k)  - Recursive descent parser combinator library
- [Result4k](result4k) - A usable Result type
- [Time4k](time4k) - Clock and deterministic scheduler
- [Tuples4k](tuples4k) - Tuple classes

## Installation
In Gradle, install the BOM and then any other modules in the dependency block: 

```groovy
implementation platform("dev.forkhandles:forkhandles-bom:X.Y.Z")
implementation "dev.forkhandles:result4k"
```
