package dev.forkhandles.fs4k

import java.io.InputStream

interface Fs4kFile {
    fun create(): Boolean
    fun delete(): Boolean

    interface Text : Fs4kFile {
        var content: String
        companion object
    }

    interface Binary : Fs4kFile {
        var content: InputStream
        companion object
    }
}
