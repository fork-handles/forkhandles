package dev.forkhandles.fs4k

import dev.forkhandles.fs4k.disk.DiskFs4k
import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

class DiskFs4kTest : Fs4kDirContract {
    override val fs4k = DiskFs4k
    override val existingParentPath: Path = Files.createTempDirectory("fs4k").toFile().toPath()
    override val nonExistingPath: Path = File(existingParentPath.toFile(), UUID.randomUUID().toString()).toPath()
}
