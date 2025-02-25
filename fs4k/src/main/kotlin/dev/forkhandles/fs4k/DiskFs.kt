package dev.forkhandles.fs4k

import dev.forkhandles.fs4k.CreateMode.Automatic
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream
import java.nio.file.Path

fun Fs4k.Companion.Disk(dir: File, createMode: CreateMode = Automatic): Fs4k = object : Fs4k {
    init {
        if (createMode == Automatic) create()
    }

    override fun create() = dir.mkdirs()

    override fun delete() = dir.deleteRecursively()

    override fun binary(name: String, fn: Fs4kFile.Binary.() -> Unit): Fs4kFile.Binary =
        Fs4kFile.Binary.Disk(File(dir, name), createMode).apply(fn)

    override fun text(name: String, fn: Fs4kFile.Text.() -> Unit): Fs4kFile.Text =
        Fs4kFile.Text.Disk(File(dir, name), createMode).apply(fn)

    override fun dir(name: String, fn: Fs4k.() -> Unit): Fs4k = Fs4k.Disk(File(dir, name), createMode).apply(fn)
}

fun Fs4k.Companion.Disk(dir: String, createMode: CreateMode = Automatic) = Disk(File(dir), createMode)
fun Fs4k.Companion.Disk(path: Path, createMode: CreateMode = Automatic) = Disk(path.toFile(), createMode)

private fun Fs4kFile.Text.Companion.Disk(file: File, createMode: CreateMode) = object : Fs4kFile.Text {
    private var toWrite: String = ""

    init {
        if (createMode == Automatic) create()
    }

    override fun create() = with(file) {
        parentFile.mkdirs()
        if (!exists()) {
            val created = createNewFile()
            if (created) file.writeText(toWrite)
            created
        } else {
            writeText(toWrite)
            true
        }
    }

    override fun delete() = file.delete()

    override var content: String
        get() = file.reader().readText()
        set(value) {
            toWrite = value
            if (createMode == Automatic) create()
        }
}

private fun Fs4kFile.Binary.Companion.Disk(file: File, createMode: CreateMode) = object : Fs4kFile.Binary {
    private var toWrite: InputStream = "".byteInputStream()

    init {
        if (createMode == Automatic) create()
    }

    override fun create() = with(file) {
        parentFile.mkdirs()
        if (!exists()) {
            val created = createNewFile()
            if (created) toWrite.copyTo(FileOutputStream(file))
            created
        } else {
            toWrite.copyTo(FileOutputStream(file))
            true
        }
    }

    override fun delete() = file.delete()

    override var content: InputStream
        get() = FileInputStream(file)
        set(value) {
            toWrite = value
            if (createMode == Automatic) create()
        }
}
