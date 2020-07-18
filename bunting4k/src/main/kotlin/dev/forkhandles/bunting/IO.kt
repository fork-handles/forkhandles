package dev.forkhandles.bunting

import dev.forkhandles.bunting.Visibility.Public
import dev.forkhandles.bunting.Visibility.Secret

interface IO {
    fun read(visibility: Visibility): String
    fun write(message: String)
}

object ConsoleIO : IO {
    override fun read(visibility: Visibility): String = when (visibility) {
        Secret -> System.console()?.readPassword()?.let { String(it) } ?: read(Public)
        Public -> readLine() ?: throw IllegalArgumentException("Failed to read input")
    }

    override fun write(message: String) = print(message)
}