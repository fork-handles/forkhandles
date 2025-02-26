package dev.forkhandles.fs4k.disk

import dev.forkhandles.fs4k.CreateMode
import dev.forkhandles.fs4k.Fs4kFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

internal class BinaryFile(
    private val file: File,
    private val createMode: CreateMode
) : Fs4kFile.Binary {
    private var toWrite: InputStream = "".byteInputStream()

    init {
        if (createMode == CreateMode.Automatic) create()
    }

    override fun exists() = file.exists()

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
            if (createMode == CreateMode.Automatic) create()
        }
}
