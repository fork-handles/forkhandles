import dev.forkhandles.bunting.Bunting
import dev.forkhandles.bunting.enum
import dev.forkhandles.bunting.int
import dev.forkhandles.bunting.use

class MyGreatFlags(args: Array<String>) : Bunting(args) {
    val view by command(::ViewFlags, "view things")
    val list by command(::ListFlags, "list things")
    val delete by command(::DeleteFlags, "delete things")

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
class ViewFlags(args: Array<String>) : Bunting(args)

class ListFlags(args: Array<String>) : Bunting(args) {
    val includeDates by switch("Switch relevant to this mode")
}
class DeleteFlags(args: Array<String>) : Bunting(args)

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
