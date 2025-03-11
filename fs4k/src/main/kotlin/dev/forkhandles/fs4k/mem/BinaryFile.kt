package dev.forkhandles.fs4k.mem

import dev.forkhandles.fs4k.CreateMode
import dev.forkhandles.fs4k.Fs4kFile
import dev.forkhandles.fs4k.Fs4kPath
import java.io.BufferedReader
import java.io.InputStream

internal class BinaryFile(
    private val state: MutableMap<Fs4kPath, String?>,
    private val path: Fs4kPath,
    private val createMode: CreateMode
) : Fs4kFile.Binary {
    private var toWrite: InputStream = "".byteInputStream()

    init {
        if (createMode == CreateMode.Automatic) create()
    }

    override fun exists() = state.containsKey(path)

    override fun create() = state
        .set(path, toWrite.bufferedReader().use(BufferedReader::readText))
        .let { state.ensureParents(path) }
        .let { true }

    override fun delete() = state.remove(path).let { true }

    override var content: InputStream
        get() = (state[path]?: "").byteInputStream()
        set(value) {
            toWrite = value
            if (createMode == CreateMode.Automatic) create()
        }
}
