package dev.forkhandles.fs4k

import java.io.File
import java.nio.file.Files
import java.util.UUID

class DiskFsTest : FsContract {
    override val rootPath = File(Files.createTempDirectory("fs4k").toFile(), UUID.randomUUID().toString()).path!!
}
