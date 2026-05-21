package dev.forkhandles.fs4k

/**
 * Represents a directory in the filesystem.
 */
interface Fs4kDir: Fs4kEntity {

    /**
     * Manipulate the directory filesystem.
     */
    operator fun invoke(fn: Fs4kDir.() -> Unit) = apply(fn)

    /**
     * Create a binary file in the directory.
     */
    @IgnorableReturnValue
    fun binary(name: String, fn: Fs4kFile.Binary.() -> Unit = {}): Fs4kFile.Binary

    /**
     * Create a text file in the directory.
     */
    @IgnorableReturnValue
    fun text(name: String, fn: Fs4kFile.Text.() -> Unit = {}): Fs4kFile.Text

    /**
     * Create a subdirectory in the directory.
     */
    @IgnorableReturnValue
    fun dir(name: String, fn: Fs4kDir.() -> Unit = {}): Fs4kDir
}

