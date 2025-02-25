package dev.forkhandles.fs4k.disk

import dev.forkhandles.fs4k.CreateMode
import dev.forkhandles.fs4k.Fs4kFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

internal class BinaryFile(
    private val createMode: CreateMode,
    private val file: File
) : Fs4kFile.Binary {
    private var toWrite: InputStream = "".byteInputStream()

    init {
        if (createMode == CreateMode.Automatic) create()
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
            if (createMode == CreateMode.Automatic) create()
        }
}
