# Bunting4k

Simple, typesafe, testable command line flags.

## Why?
This nano-library provides a simple way to set command line options using required, defaulted or switch-based options. Use the long or short names to set the options, or pass "--help" for the docs.

```kotlin

class MyGreatFlags(args: Array<String>) : Bunting(args) {
    enum class Command {
        first, second
    }
    val command by option("This is a top level command").enum<Command>()
    val verbose by switch("This is a switch")
    val user by option("This is a required option")
    val password by option("This is another required option")
    val version by option().int().defaultsTo("0")
}

object SingleOption {
    @JvmStatic
    // run the main with: java (...) SingleOption --user foo --password bar
    fun main(ignored: Array<String>) = MyGreatFlags(arrayOf("--user", "foo", "-p", "bar")).use {
        println(verbose)    // false    <-- because not set
        println(user)       // foo      <-- passed value (full name)
        println(password)   // bar      <-- passed value (short name)
        println(version)    // 0        <-- defaulted value
    }
}

object MultiOption {
    @JvmStatic
    // run the main with: java (...) MultiOptionKt --command a --user foo --password bar
    fun main(ignored: Array<String>) = MyGreatFlags(arrayOf("--command", "a", "--user", "foo", "-p", "bar")).use {
        when(command) {
            first -> {
                println(verbose)    // false    <-- because not set
                println(user)       // foo      <-- passed value (full name)
            }
            second -> {
                println(password)   // bar      <-- passed value (short name)
                println(version)    // 0        <-- defaulted value
            }
        }
    }
}
```

