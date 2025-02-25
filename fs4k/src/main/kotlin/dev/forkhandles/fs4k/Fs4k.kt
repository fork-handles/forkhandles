package dev.forkhandles.fs4k

import dev.forkhandles.fs4k.CreateMode.Automatic
import java.nio.file.Path

interface Fs4k {
    operator fun invoke(fn: Fs4k.() -> Unit) = apply(fn)

    fun create(): Boolean

    fun delete(): Boolean

    fun binary(name: String, fn: Fs4kFile.Binary.() -> Unit = {}): Fs4kFile.Binary

    fun text(name: String, fn: Fs4kFile.Text.() -> Unit = {}): Fs4kFile.Text

    fun dir(name: String, fn: Fs4k.() -> Unit = {}): Fs4k

    companion object {
        fun dir(
            path: Path,
            fs4k: (Path, CreateMode) -> Fs4k = Companion::Disk,
            createMode: CreateMode = Automatic,
            fn: Fs4k.() -> Unit = {}
        ): Fs4k = fs4k(path, createMode).apply(fn)

        fun dir(
            path: String = ".",
            fs4k: (Path, CreateMode) -> Fs4k = Companion::Disk,
            createMode: CreateMode = Automatic,
            fn: Fs4k.() -> Unit = {}
        ) = dir(Path.of(path), fs4k, createMode, fn)
    }
}
