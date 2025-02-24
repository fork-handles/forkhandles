package dev.forkhandles.fs4k

import dev.forkhandles.fs4k.CreateMode.Automatic
import java.io.File

class Fs4k internal constructor(private val rootDir: File) {

    private val execute = mutableListOf<() -> Unit>({ rootDir.mkdirs() })

    fun file(name: String, content: String) =
        execute.add {
            File(rootDir, name).apply { writeText(content) }
        }

    fun dir(name: String, fn: Fs4k.() -> Unit) = execute.add {
        Fs4k(File(rootDir, name)).apply(fn).create()
    }

    fun create() = execute.forEach { it() }

    companion object {
        fun dir(rootDir: String, createMode: CreateMode = Automatic, fn: Fs4k.() -> Unit) =
            dir(File(rootDir), createMode, fn)

        fun dir(rootDir: File = File("."), createMode: CreateMode = Automatic, fn: Fs4k.() -> Unit) =
            Fs4k(rootDir).apply(fn)
                .apply {
                    if (createMode == Automatic) create()
                }
    }
}

