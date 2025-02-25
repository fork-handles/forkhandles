package dev.forkhandles.fs4k

import dev.forkhandles.fs4k.CreateMode.Automatic
import java.nio.file.Path

interface Fs {
    fun delete(): Boolean

    fun file(name: String, fn: FsFile.() -> Unit = {}): FsFile
    fun dir(name: String, fn: Fs.() -> Unit = {}): Fs

    fun create()

    companion object
}

interface FsFile {
    fun delete(): Boolean
    var content: String
}

fun fsDir(path: Path, fs: (Path, CreateMode) -> Fs = ::DiskFs, createMode: CreateMode = Automatic,  fn: Fs.() -> Unit = {}): Fs = fs(path, createMode).apply(fn)

fun fsDir(path: String = ".", fs: (Path, CreateMode) -> Fs = ::DiskFs, createMode: CreateMode = Automatic, fn: Fs.() -> Unit = {}) = fsDir(Path.of(path), fs, createMode, fn)
