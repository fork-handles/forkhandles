package dev.forkhandles.fs4k.disk

import dev.forkhandles.fs4k.CreateMode
import dev.forkhandles.fs4k.Fs4kDir
import dev.forkhandles.fs4k.Fs4kFile
import java.io.File

internal class Dir(private val dir: File, private val createMode: CreateMode) : Fs4kDir {
    init {
        if (createMode == CreateMode.Automatic) create()
    }

    override fun exists() = dir.exists()

    override fun create() = dir.mkdirs()

    override fun delete() = dir.deleteRecursively()

    override fun binary(name: String, fn: Fs4kFile.Binary.() -> Unit): Fs4kFile.Binary =
        BinaryFile(File(dir, name), createMode).apply(fn)

    override fun text(name: String, fn: Fs4kFile.Text.() -> Unit): Fs4kFile.Text =
        TextFile(File(dir, name), createMode).apply(fn)

    override fun dir(name: String, fn: Fs4kDir.() -> Unit): Fs4kDir =
        Dir(File(dir, name), createMode).apply(fn)
}
