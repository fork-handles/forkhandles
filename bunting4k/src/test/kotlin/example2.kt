import dev.forkhandles.bunting.Bunting
import dev.forkhandles.bunting.use

class SubCommand(args: Array<String>) : Bunting(args) {
    val boogie by switch()
    val command2 by command(::SubCommand2)
}

class SubCommand2(args: Array<String>) : Bunting(args) {
    val boogie2 by switch()
}

class Global(args: Array<String>) : Bunting(args) {
    val verbose by option()

    val command by command(::SubCommand)
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