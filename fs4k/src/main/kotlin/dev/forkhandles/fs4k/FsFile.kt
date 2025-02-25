package dev.forkhandles.fs4k

interface FsFile {
    fun create(): Boolean
    fun delete(): Boolean
    var content: String
}
