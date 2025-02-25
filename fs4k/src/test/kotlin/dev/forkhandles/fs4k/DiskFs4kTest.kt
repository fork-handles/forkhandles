package dev.forkhandles.fs4k

import dev.forkhandles.fs4k.disk.DiskFs4k
import java.io.File
import java.nio.file.Files
import java.util.UUID

class DiskFs4kTest : Fs4kDirContract {
    override val fs4k = DiskFs4k

    override val existingParentPath: Fs4kPath =
        Fs4kPath.of(Files.createTempDirectory("fs4k").toFile().toPath().toString())

    override val nonExistingPath: Fs4kPath =
        Fs4kPath.of(File(existingParentPath.toFile(), UUID.randomUUID().toString()).toPath().toString())

    override fun exists(path: Fs4kPath, name: String): Boolean =
        File(path.toFile(), name).exists()
}
