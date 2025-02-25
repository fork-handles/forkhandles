package dev.forkhandles.fs4k

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

}

