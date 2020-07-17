package dev.forkhandles.bunting

interface IO {
    fun read(): String
    fun write(message: String)
    fun writeln(message: String) = write(message + "\n")
}

object ConsoleIO : IO {
    override fun read() = readLine() ?: throw IllegalArgumentException("Failed to read input")
    override fun write(message: String) = print(message)
}
