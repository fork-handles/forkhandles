package dev.forkhandles.bunting

interface IO {
    fun read(masked: Boolean): String
    fun write(message: String)
}

object ConsoleIO : IO {
    override fun read(masked: Boolean): String = when {
        masked -> System.console()?.readPassword()?.let { String(it) } ?: read(false)
        else -> readLine() ?: throw IllegalArgumentException("Failed to read input")
    }

    override fun write(message: String) = print(message)
}


fun main() {
    val console = System.console()
    val message = console.readPassword("")
    println(message)
}