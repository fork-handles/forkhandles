package dev.forkhandles.fs4k

import dev.forkhandles.fs4k.CreateMode.Automatic
import java.io.File
import java.nio.file.Path

class DiskFs private constructor(private val dir: File, private val createMode: CreateMode = Automatic) : Fs {
    constructor(dir: String, createMode: CreateMode = Automatic) : this(File(dir), createMode)
    constructor(path: Path, createMode: CreateMode = Automatic) : this(path.toFile(), createMode)

    init {
        if (createMode == Automatic) create()
    }

    override fun create() = dir.mkdirs()

    override fun delete() = dir.deleteRecursively()

    private class DiskFile(private val file: File, private val createMode: CreateMode) : FsFile {
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
                file.writeText(content)
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

    override fun file(name: String, fn: FsFile.() -> Unit): FsFile =
        DiskFile(File(dir, name), createMode).apply(fn)

    override fun dir(name: String, fn: Fs.() -> Unit): Fs = DiskFs(File(dir, name), createMode).apply(fn)
}
