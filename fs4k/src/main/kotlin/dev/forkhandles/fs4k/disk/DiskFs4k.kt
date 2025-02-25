package dev.forkhandles.fs4k.disk

import dev.forkhandles.fs4k.CreateMode
import dev.forkhandles.fs4k.Fs4k
import dev.forkhandles.fs4k.Fs4kDir
import dev.forkhandles.fs4k.Fs4kPath
import java.io.File
import java.nio.file.Path

/**
 * A filesystem that uses the local disk.
 */
object DiskFs4k : Fs4k {

    override fun invoke(dir: String, createMode: CreateMode): Fs4kDir = Dir(File(dir), createMode)

    override fun invoke(path: Fs4kPath, createMode: CreateMode): Fs4kDir = Dir(Path.of(path.value).toFile(), createMode)
}
