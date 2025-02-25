package dev.forkhandles.fs4k

import java.io.File
import java.nio.file.Path

class DiskFs private constructor(private val dir: File, private val creationFunctons: MutableList<() -> Boolean>) : Fs {
    constructor(dir: File) : this(dir, mutableListOf())
    constructor(path: Path) : this(path.toFile())

    init {
        creationFunctons += {
            println("creating DIR ${dir.absolutePath}")

            dir.mkdirs()
        }
    }

    override fun create() = creationFunctons.fold(true) { acc, next ->
        acc && next()
    }

    override fun delete() = dir.delete()

    private class DiskFile(private val file: File, private val execute: MutableList<() -> Boolean>) : FsFile {

        init {
            execute += ::create
        }

        override fun create() = with(file) {
            if (!exists()) createNewFile().also { println(it) } else true
        }

        override fun delete() = file.delete()

        override var content: String
            get() = file.reader().readText()
            set(value) {
                execute += {
                    create() && runCatching { file.writeText(value) }.isSuccess
                }
            }
    }

    override fun file(name: String, fn: FsFile.() -> Unit): FsFile = DiskFile(File(dir, name), creationFunctons).apply(fn)

    override fun dir(name: String, fn: Fs.() -> Unit): Fs = DiskFs(File(dir, name), creationFunctons).apply(fn)
}
