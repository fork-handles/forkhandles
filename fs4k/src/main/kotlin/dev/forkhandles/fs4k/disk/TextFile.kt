package dev.forkhandles.fs4k.disk

import dev.forkhandles.fs4k.CreateMode
import dev.forkhandles.fs4k.Fs4kFile
import java.io.File

internal class TextFile(private val createMode: CreateMode, private val file: File) : Fs4kFile.Text {
    private var toWrite: String = ""

    init {
        if (createMode == CreateMode.Automatic) create()
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
            if (createMode == CreateMode.Automatic) create()
        }
}
