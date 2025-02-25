package dev.forkhandles.fs4k.disk

import dev.forkhandles.fs4k.CreateMode
import dev.forkhandles.fs4k.Fs4k
import dev.forkhandles.fs4k.Fs4kDir
import java.io.File
import java.nio.file.Path

/**
 * A filesystem that uses the local disk.
 */
object DiskFs4k : Fs4k {

    override fun invoke(dir: String, createMode: CreateMode): Fs4kDir = Dir(createMode, File(dir))

    override fun invoke(path: Path, createMode: CreateMode): Fs4kDir = Dir(createMode, path.toFile())
}
