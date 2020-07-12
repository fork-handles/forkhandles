# Bunting4k

Simple, typesafe, testable command line flags.

## Why?
This nano-library provides a simple way to set command line options using required, defaulted or switch-based options. Set commands, use the long or short names to set the options and flags, or pass "--help" for the docs.

```kotlin
import dev.forkhandles.bunting.Bunting
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
    val user by option("This is a required option")
    val password by option("This is another required option")
    val version by option().int().defaultsTo("0")
    val level by option().enum<LogLevel>().defaultsTo("warn")
}

// Some sub commands - these can define their own flags
class ViewFlags(args: Array<String>) : Bunting(args, "view things")
class ListFlags(args: Array<String>) : Bunting(args, "list things") {
    val includeDates by switch("Switch relevant to this mode")
}
class DeleteFlags(args: Array<String>) : Bunting(args, "delete things")

object SingleOption {
    @JvmStatic
    // run the main with: java (...) SingleOption --user foo --password bar
    fun main(ignored: Array<String>) = MyGreatFlags(arrayOf("--user", "foo", "-p", "bar")).use {
        println(insecure)   // false    <-- because not set
        println(user)       // foo      <-- passed value (full name)
        println(password)   // bar      <-- passed value (short name)
        println(version)    // 0        <-- defaulted value
        println(level)      // warn        <-- defaulted value
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
            println(password)           // bar      <-- passed value (short name)
            println(version)            // 0        <-- defaulted value
        }
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
Usage: AskForHelp [flags] [options]
[flags]:
  delete                                delete things
  list                                  list things
    [options]:
      -i, --includeDates                Switch relevant to this mode
  view                                  view things
[options]:
  -i, --insecure                        This is a switch
  -l, --level                           Option choice: [debug, warn]. Defaults to "warn" (LOGLEVEL)
  -p, --password                        This is another required option (STRING)
  -u, --user                            This is a required option (STRING)
  -v, --version                         Defaults to "0" (INT)
  -h, --help                            Show this message and exit
```

