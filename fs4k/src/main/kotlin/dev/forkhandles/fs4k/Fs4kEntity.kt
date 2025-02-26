package dev.forkhandles.fs4k

interface Fs4kEntity {
    fun create(): Boolean
    fun delete(): Boolean
    fun exists(): Boolean
}
