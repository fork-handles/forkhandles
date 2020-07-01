# Bunting4k

Simple, typesafe, testable command line flags.

## Why?
This nano-library provides a simple way to set command line options using required, defaulted or switch-based options. Use the long or short names to set the options, or pass "--help" for the docs.

```kotlin
class MyGreatFlags(args: Array<String>) : Bunting(args) {
    val verbose by switch("This is a switch")
    val user by option("This is a required option")
    val password by option("This is another required option")
    val version by option().int().defaultsTo("0")
}

// run the main with: java (...) Mainkt --user foo --password bar
fun main() = MyGreatFlags(arrayOf("--user", "foo", "--p", "bar")).use {
    println(verbose)    // false    <-- because not set
    println(user)       // foo      <-- passed value (full name)
    println(password)   // bar      <-- passed value (short name)
    println(version)    // 0        <-- defaulted value
}
```