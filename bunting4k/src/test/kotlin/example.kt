import dev.forkhandles.bunting.Bunting
import dev.forkhandles.bunting.int
import dev.forkhandles.bunting.use

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