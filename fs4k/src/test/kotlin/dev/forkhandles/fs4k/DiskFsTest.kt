package dev.forkhandles.fs4k

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

class DiskFsTest : Fs4kContract {
    override val fs4k = { path: Path, mode: CreateMode -> Fs4k.Disk(path, mode) }
    override val existingParentPath: Path = Files.createTempDirectory("fs4k").toFile().toPath()
    override val nonExistingPath: Path = File(existingParentPath.toFile(), UUID.randomUUID().toString()).toPath()
}
