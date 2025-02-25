package dev.forkhandles.fs4k

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

class DiskFs4kTest : Fs4kDirContract {
    override val fs4k = { path: Path, mode: CreateMode -> Fs4kDir.Disk(path, mode) }
    override val existingParentPath: Path = Files.createTempDirectory("fs4k").toFile().toPath()
    override val nonExistingPath: Path = File(existingParentPath.toFile(), UUID.randomUUID().toString()).toPath()
}
