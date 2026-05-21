package dev.forkhandles.fs4k

interface Fs4kEntity {
    @IgnorableReturnValue
    fun create(): Boolean
    
    @IgnorableReturnValue
    fun delete(): Boolean
    
    fun exists(): Boolean
}
