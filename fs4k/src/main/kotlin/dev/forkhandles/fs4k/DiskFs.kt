package dev.forkhandles.fs4k

import java.io.File
import java.nio.file.Path

class DiskFs(private val dir: File, private val createMode: CreateMode = CreateMode.Automatic) : Fs {
    constructor(path: Path, createMode: CreateMode = CreateMode.Automatic) : this(path.toFile(), createMode)

    override fun create() = dir.mkdirs()

    override fun delete() = dir.delete()

    private class DiskFile(private val file: File) : FsFile {
        override fun create() = with(file) {
            mkdirs()
            createNewFile()
        }

        override fun delete() = file.delete()

        override var content: String
            get() = file.reader().readText()
            set(value) = with(file) {
                mkdirs()
                writeText(value)
            }
    }

    override fun file(name: String, fn: FsFile.() -> Unit): FsFile = DiskFile(File(dir, name))

    override fun dir(name: String, fn: Fs.() -> Unit): Fs = DiskFs(File(dir, name))
}
