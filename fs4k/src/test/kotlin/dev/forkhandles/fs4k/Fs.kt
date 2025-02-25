package dev.forkhandles.fs4k

import java.io.File
import java.nio.file.Path

interface Fs {
    fun delete(): Boolean

    fun file(name: String, fn: FsFile.() -> Unit = {}): FsFile
    fun dir(name: String, fn: Fs.() -> Unit = {}): Fs
}

interface FsFile {
    fun delete(): Boolean
    var content: String
}

fun fsDir(path: Path, fs: (Path) -> Fs = ::DiskFs, fn: Fs.() -> Unit = {}): Fs = fs(path).apply(fn)

fun fsDir(path: String = ".", fs: (Path) -> Fs = ::DiskFs, fn: Fs.() -> Unit = {}) = fsDir(Path.of(path), fs, fn)

class DiskFs(private val dir: File) : Fs {
    constructor(path: Path) : this(path.toFile())

    override fun delete() = dir.delete()

    private class DiskFile(private val file: File) : FsFile {
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
