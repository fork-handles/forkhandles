import dev.forkhandles.bunting.Bunting
import dev.forkhandles.bunting.use

class MyGreatFlags(args: Array<String>) : Bunting(args) {
    val user by requiredFlag()
    val password by requiredFlag()
    val version by defaultedFlag("0.0.0")
}

// run the main with: java (...) Mainkt --user foo --password bar
fun main() = MyGreatFlags(arrayOf("--user", "foo", "--password", "bar", "")).use {
    println(user) // foo
    println(password) // bar
    println(version) // 0.0.0
}