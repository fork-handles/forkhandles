package dev.forkhandles.fs4k

import dev.forkhandles.fs4k.CreateMode.Automatic
import java.nio.file.Path

interface Fs {

    operator fun invoke(fn: Fs.() -> Unit) = apply(fn)

    fun create(): Boolean
    fun delete(): Boolean

    fun file(name: String, fn: FsFile.() -> Unit = {}): FsFile
    fun dir(name: String, fn: Fs.() -> Unit = {}): Fs

    companion object {
        fun dir(
            path: Path,
            fs: (Path, CreateMode) -> Fs = ::DiskFs,
            createMode: CreateMode = Automatic,
            fn: Fs.() -> Unit = {}
        ): Fs = fs(path, createMode).apply(fn)

        fun dir(
            path: String = ".",
            fs: (Path, CreateMode) -> Fs = ::DiskFs,
            createMode: CreateMode = Automatic,
            fn: Fs.() -> Unit = {}
        ) = dir(Path.of(path), fs, createMode, fn)
    }
}

