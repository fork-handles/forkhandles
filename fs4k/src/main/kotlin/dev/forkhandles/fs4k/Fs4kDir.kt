package dev.forkhandles.fs4k

import dev.forkhandles.fs4k.CreateMode.Automatic
import dev.forkhandles.fs4k.disk.DiskFs4k
import java.nio.file.Path

/**
 * Represents a directory in the filesystem.
 */
interface Fs4kDir {

    /**
     * Manipulate the directory filesystem.
     */
    operator fun invoke(fn: Fs4kDir.() -> Unit) = apply(fn)

    /**
     * Create the directory.
     */
    fun create(): Boolean

    /**
     * Delete the directory.
     */
    fun delete(): Boolean

    /**
     * Create a binary file in the directory.
     */
    fun binary(name: String, fn: Fs4kFile.Binary.() -> Unit = {}): Fs4kFile.Binary

    /**
     * Create a text file in the directory.
     */
    fun text(name: String, fn: Fs4kFile.Text.() -> Unit = {}): Fs4kFile.Text

    /**
     * Create a subdirectory in the directory.
     */
    fun dir(name: String, fn: Fs4kDir.() -> Unit = {}): Fs4kDir

    companion object {
        /**
         * Create a directory in the filesystem.
         */
        fun dir(
            path: Path,
            fs4k: Fs4k = DiskFs4k,
            createMode: CreateMode = Automatic,
            fn: Fs4kDir.() -> Unit = {}
        ): Fs4kDir = fs4k(path, createMode).apply(fn)

        /**
         * Create a directory in the filesystem.
         */
        fun dir(
            path: String = ".",
            fs4k: Fs4k = DiskFs4k,
            createMode: CreateMode = Automatic,
            fn: Fs4kDir.() -> Unit = {}
        ) = dir(Path.of(path), fs4k, createMode, fn)
    }
}
