package dev.forkhandles.fs4k

import dev.forkhandles.fs4k.CreateMode.Automatic
import dev.forkhandles.fs4k.CreateMode.Manual
import dev.forkhandles.fs4k.Fs4k.Companion.dir
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Files

class Fs4kTest {

    @Test
    fun `creates files automatically`() {
        val file = Files.createTempDirectory("fs4k").toFile()
        dir(file, createMode = Automatic) {
            file("plainfile.txt") {
                "hello"
            }

            dir("directory") {
                file("file2.html") {
                    "<html/>"
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

        val fs4k = dir(file, createMode = Manual) {
            file("plainfile.txt") {
                "hello"
            }

            dir("directory") {
                file("file2.html") {
                    "<html/>"
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
