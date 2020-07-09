import MyGreatFlags.Command.delete
import MyGreatFlags.Command.list
import dev.forkhandles.bunting.Bunting
import dev.forkhandles.bunting.enum
import dev.forkhandles.bunting.int
import dev.forkhandles.bunting.use

class MyGreatFlags(args: Array<String>) : Bunting(args) {
    enum class Command {
        list, delete
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
    // run the main with: java (...) MultiOptionKt --command list --user foo --password bar
    fun main(ignored: Array<String>) = MyGreatFlags(arrayOf("--command", "list", "--user", "foo", "-p", "bar")).use {
        when (command) {
            list -> {
                println(verbose)    // false    <-- because not set
                println(user)       // foo      <-- passed value (full name)
            }
            delete -> {
                println(password)   // bar      <-- passed value (short name)
                println(version)    // 0        <-- defaulted value
            }
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
