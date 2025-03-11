package dev.forkhandles.fs4k.disk

import dev.forkhandles.fs4k.Fs4kDir
import dev.forkhandles.fs4k.Fs4kDirContract
import dev.forkhandles.fs4k.Fs4kPath
import dev.forkhandles.fs4k.mem.MemFs4k
import dev.forkhandles.fs4k.mem.add
import java.util.*

class MemFs4kTest : Fs4kDirContract {
    override val fs4k = MemFs4k

    override val existingParentPath =
        Fs4kPath.of("/")

    override val nonExistingPath =
        Fs4kPath.of(UUID.randomUUID().toString())

    override fun Fs4kDir.content(path: Fs4kPath, name: String) = fs4k.state[path.add(name)] ?: ""

    override fun Fs4kDir.exists(path: Fs4kPath, name: String): Boolean = fs4k.state.containsKey(path.add(name))

    override fun Fs4kDir.exists(path: Fs4kPath) = fs4k.state.containsKey(path)
}
