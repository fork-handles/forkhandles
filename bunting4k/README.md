# Bunting4k

<a href="https://mvnrepository.com/artifact/dev.forkhandles"><img alt="Download" src="https://img.shields.io/maven-central/v/dev.forkhandles/forkhandles-bom"></a>
[![.github/workflows/build.yaml](https://github.com/fork-handles/forkhandles/actions/workflows/build.yaml/badge.svg)](https://github.com/fork-handles/forkhandles/actions/workflows/build.yaml)
<a href="https://codecov.io/gh/fork-handles/forkhandles"><img src="https://codecov.io/gh/fork-handles/forkhandles/branch/trunk/graph/badge.svg"/></a>
<a href="http//www.apache.org/licenses/LICENSE-2.0"><img alt="GitHub license" src="https://img.shields.io/badge/license-Apache%20License%202.0-blue.svg?style=flat"></a>
<a href="http://kotlinlang.org"><img alt="kotlin" src="https://img.shields.io/badge/kotlin-1.8-blue.svg"></a>
<a href="https://codebeat.co/projects/github-com-fork-handles-forkhandles-trunk"><img alt="codebeat badge" src="https://codebeat.co/badges/5b369ed4-af27-46f4-ad9c-a307d900617e"></a>

Simple, typesafe, testable command line flags.

## Installation

In Gradle, install the ForkHandles BOM and then this module in the dependency block:

```kotlin
implementation(platform("dev.forkhandles:forkhandles-bom:X.Y.Z"))
implementation("dev.forkhandles:bunting4k")
```

## Why?
This nano-library provides a simple way to set command line options using required, defaulted or switch-based options. Set commands, use the long or short names to set the options and flags, or pass "--help" for the docs.

```kotlin
import MyGreatFlags.LogLevel.warn
import dev.forkhandles.bunting.Bunting
import dev.forkhandles.bunting.boolean
import dev.forkhandles.bunting.enum
import dev.forkhandles.bunting.int
import dev.forkhandles.bunting.use

// Top level command flags
class MyGreatFlags(args: Array<String>) : Bunting(args) {
    val view by command(::ViewFlags)
    val list by command(::ListFlags)
    val delete by command(::DeleteFlags)

    enum class LogLevel {
        debug, warn
    }

    val insecure by switch("This is a switch")
    val user by option("This is optional")
    val file by option("This is a required option").required()
    val version by option().int().defaultsTo(0)
    val prompt by option("This is prompted value").prompted()
    val secret by option("This is secret value").int().secret().prompted()
    val level by option().enum<LogLevel>().defaultsTo(warn)
}

// Some sub commands - these can define their own flags
class ViewFlags(args: Array<String>) : Bunting(args, "View things")
class ListFlags(args: Array<String>) : Bunting(args, "List things") {
    val includeDates by switch("Switch relevant to this mode")
}

class DeleteFlags(args: Array<String>) : Bunting(args, "delete things")

object SingleOption {
    @JvmStatic
    // run the main with: java (...) SingleOption --user foo --password bar
    fun main(ignored: Array<String>) = MyGreatFlags(arrayOf("-p", "bar")).use {
        println(insecure)   // false    <-- because not set
        println(user)       // foo      <-- passed value (full name)
        println(file)   // bar      <-- passed value (short name)
        println(version)    // 0        <-- defaulted value
        println(level)      // warn     <-- defaulted value
    }
}

object SubCommands {
    @JvmStatic
    // run the main with: java (...) SubCommands --command list --user foo --password bar
    fun main(ignored: Array<String>) = MyGreatFlags(arrayOf("list", "--user", "foo", "-p", "bar")).use {
        list.use {
            println(insecure)       // false    <-- because not set
            println(user)           // foo      <-- passed value (full name)
            println(includeDates)   // false    <-- local switch
        }

        delete.use {
            println(file)           // bar      <-- passed value (short name)
        }

        view.use {
            println(version)        // 0        <-- defaulted value
        }
    }
}

object PromptedForValue {
    @JvmStatic
    // run the main with: java (...) PromptedForValue
    fun main(ignored: Array<String>) = MyGreatFlags(arrayOf()).use {
        println(prompt)
    }
}

object SecretValue {
    @JvmStatic
    // run the main with: java (...) SecretValue (note that when run in IDE, the masking will not work. Run from command line is ok...
    fun main(ignored: Array<String>) = MyGreatFlags(arrayOf("-s", "123")).use {
        println(secret)
    }
}

object AskForHelp {
    @JvmStatic
    // run the main with: java (...) AskForHelp --help
    fun main(ignored: Array<String>) = MyGreatFlags(arrayOf("--help")).use {
        // doesn't matter
    }
}
```

Help output formatted as:
```
Usage: AskForHelp [commands] [options]
[commands]:
  delete                                
    delete things
  list                                  
    List things
    [options]:
      -i, --includeDates                Switch relevant to this mode
  view                                  
    View things
[options]:
  -f, --file                            This is a required option (STRING)
  -i, --insecure                        This is a switch
  -l, --level                           Option choice: [debug, warn]. Defaults to "warn" (LOGLEVEL)
  -p, --prompt                          This is prompted value (STRING)
  -s, --secret                          This is secret value (INT)
  -u, --user                            This is optional (STRING?)
  -v, --version                         Defaults to "0" (INT)
  -h, --help                            Show this message and exit
```
