import dev.forkhandles.bunting.Bunting
import dev.forkhandles.bunting.use

class SubCommand(args: Array<String>) : Bunting(args) {
    val boogie by switch("Some boogie switch")
    val command2 by command(::SubCommand2, "Grandchild program")
}

class SubCommand2(args: Array<String>) : Bunting(args) {
    val boogie2 by switch("Some boogie2 switch")
}

class Global(args: Array<String>) : Bunting(args, "Global is a mega program") {
    val verbose by option("Show everything")
    val command by command(::SubCommand, "Child program")
}

fun main() {
    Global(arrayOf("command", "command2", "--boogie", "--help")).use {
        command.use {
            command2.use {
                println(boogie)
                println(boogie2)
            }
        }
    }
}