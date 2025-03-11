package dev.forkhandles.fs4k.mem

import dev.forkhandles.fs4k.CreateMode
import dev.forkhandles.fs4k.Fs4kFile
import dev.forkhandles.fs4k.Fs4kPath

internal class TextFile(
    private val state: MutableMap<Fs4kPath, String?>,
    private val path: Fs4kPath,
    private val createMode: CreateMode
) : Fs4kFile.Text {
    private var toWrite: String = ""

    init {
        if (createMode == CreateMode.Automatic) create()
    }

    override fun exists() = state.containsKey(path)

    override fun create() = state
        .set(path, toWrite)
        .let { state.ensureParents(path) }
        .let { true }

    override fun delete() = state.remove(path).let { true }

    override var lines: List<String>
        get() = content.split("\n")
        set(value) {
            toWrite = value.joinToString("\n")
            if (createMode == CreateMode.Automatic) create()
        }

    override var content: String
        get() = (state[path]?: "")
        set(value) {
            toWrite = value
            if (createMode == CreateMode.Automatic) create()
        }
}
