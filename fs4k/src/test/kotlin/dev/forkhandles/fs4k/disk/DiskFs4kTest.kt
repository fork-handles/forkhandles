package dev.forkhandles.fs4k.disk

import dev.forkhandles.fs4k.Fs4kDir
import dev.forkhandles.fs4k.Fs4kDirContract
import dev.forkhandles.fs4k.Fs4kPath
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

class DiskFs4kTest : Fs4kDirContract {
    override val fs4k = DiskFs4k

    override val existingParentPath =
        Fs4kPath.of(Files.createTempDirectory("fs4k").toFile().toPath().toString())

    override val nonExistingPath =
        Fs4kPath.of(File(Path.of(existingParentPath.value).toFile(), UUID.randomUUID().toString()).toPath().toString())

    override fun Fs4kDir.content(path: Fs4kPath, name: String) = File(path.value, name).reader().readText()

    override fun Fs4kDir.exists(path: Fs4kPath, name: String): Boolean =
        File(path.value, name).exists()

    override fun Fs4kDir.exists(path: Fs4kPath) = File(path.value).exists()
}
