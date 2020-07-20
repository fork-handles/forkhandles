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
    val dates by switch("Switch relevant to this mode")
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
            println(dates)          // false    <-- local switch
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
