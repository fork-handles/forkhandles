package dev.forkhandles.fs4k

import java.io.InputStream

/**
 * Represents a file in the filesystem.
 */
interface Fs4kFile : Fs4kEntity {

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
