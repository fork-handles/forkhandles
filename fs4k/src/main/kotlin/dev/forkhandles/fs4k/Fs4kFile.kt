package dev.forkhandles.fs4k

import java.io.InputStream

/**
 * Represents a file in the filesystem.
 */
interface Fs4kFile {

    /**
     * Create the file.
     */
    fun create(): Boolean

    /**
     * Delete the file.
     */
    fun delete(): Boolean

    /**
     * Represents a text file in the filesystem.
     */
    interface Text : Fs4kFile {
        var content: String
        companion object
    }

    /**
     * Represents a binary file in the filesystem.
     */
    interface Binary : Fs4kFile {
        var content: InputStream
        companion object
    }
}
