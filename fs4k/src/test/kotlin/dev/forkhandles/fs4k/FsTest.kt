package dev.forkhandles.fs4k

import dev.forkhandles.fs4k.CreateMode.Automatic
import dev.forkhandles.fs4k.CreateMode.Manual
import dev.forkhandles.fs4k.Fs.Companion.dir
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files

class FsTest {

    @Test
    fun `creates files automatically`() {
        val file = Files.createTempDirectory("fs4k").toFile()
        dir(file.path, createMode = Automatic) {
            file("plainfile.txt") {
                content = "hello"
            }

            dir("directory") {
                file("file2.html") {
                    content = "<html/>"
                }
            }
        }

        assertTrue(File(file, "plainfile.txt").exists(), "plainfile.txt should exist")
        assertTrue(File(file, "directory").exists(), "directory should exist")
        assertTrue(File(file, "directory/file2.html").exists(), "directory/file2.html should exist")
    }

    @Test
    fun `creates files manually`() {
        val file = Files.createTempDirectory("fs4k").toFile()

        val fs4k = dir(file.path, createMode = Manual) {
            file("plainfile.txt") {
                content = "hello"
            }

            dir("directory") {
                file("file2.html") {
                    content = "<html/>"
                }
            }
        }

        assertFalse(File(file, "directory").exists(), "directory should exist")

        fs4k.create()

        assertTrue(File(file, "plainfile.txt").exists(), "plainfile.txt should exist")
        assertTrue(File(file, "directory").exists(), "directory should exist")
        assertTrue(File(file, "directory/file2.html").exists(), "directory/file2.html should exist")
    }
}
