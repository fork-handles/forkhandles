package dev.forkhandles.fs4k.mem

import dev.forkhandles.fs4k.CreateMode
import dev.forkhandles.fs4k.Fs4kDir
import dev.forkhandles.fs4k.Fs4kFile
import dev.forkhandles.fs4k.Fs4kPath


internal class Dir(
    private val state: MutableMap<Fs4kPath, String?>,
    private val dir: Fs4kPath,
    private val createMode: CreateMode
) : Fs4kDir {
    init {
        if (createMode == CreateMode.Automatic) create()
    }

    override fun exists() = state.containsKey(dir)

    override fun create() = state.set(dir, null).let { true }

    override fun delete() = state
        .let { current -> current.filterKeys { it.startsWith(dir) }.onEach { (it, _) -> current.remove(it) } }
        .let { true }

    override fun binary(name: String, fn: Fs4kFile.Binary.() -> Unit): Fs4kFile.Binary =
        BinaryFile(state, dir.add(name), createMode).apply(fn)

    override fun text(name: String, fn: Fs4kFile.Text.() -> Unit): Fs4kFile.Text =
        TextFile(state, dir.add(name), createMode).apply(fn)

    override fun dir(name: String, fn: Fs4kDir.() -> Unit): Fs4kDir =
        Dir(state, dir.add(name), createMode).apply(fn)
}
