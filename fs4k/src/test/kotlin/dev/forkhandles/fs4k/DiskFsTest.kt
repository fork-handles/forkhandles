package dev.forkhandles.fs4k

import java.io.File
import java.nio.file.Files
import java.nio.file.Path
import java.util.UUID

class DiskFsTest : FsContract {
    override val existingParentPath: Path = Files.createTempDirectory("fs4k").toFile().toPath()
    override val fs = { path: Path, mode: CreateMode -> DiskFs(path, mode) }
    override val nonExistingPath: Path = File(existingParentPath.toFile(), UUID.randomUUID().toString()).toPath()
}
