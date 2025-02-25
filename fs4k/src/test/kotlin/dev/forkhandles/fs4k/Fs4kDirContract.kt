package dev.forkhandles.fs4k

import dev.forkhandles.fs4k.CreateMode.Automatic
import dev.forkhandles.fs4k.CreateMode.Manual
import dev.forkhandles.fs4k.Fs4kDir.Companion.dir
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File
import java.nio.file.Path

interface Fs4kDirContract {
    val fs4k: Fs4k

    val nonExistingPath: Path
    val existingParentPath: Path

    @Test
    fun `creates files automatically`() {
        dir(nonExistingPath, fs4k, Automatic) {
            text("plainfile.txt") {
                content = "hello"
            }
            binary("binary.png") {
                content = "goodbye".byteInputStream()
            }

            dir("directory") {
                text("file2.html") {
                    content = "<html/>"
                }
            }
        }

        val text = File(nonExistingPath.toFile(), "plainfile.txt")
        assertTrue(text.exists(), "plainfile.txt should exist")
        assertEquals("hello", text.reader().readText())

        val binary = File(nonExistingPath.toFile(), "binary.png")
        assertTrue(binary.exists(), "binary.png should exist")
        assertEquals("goodbye", binary.reader().readText())

        assertTrue(File(nonExistingPath.toFile(), "directory").exists(), "directory should exist")
        assertTrue(File(nonExistingPath.toFile(), "directory/file2.html").exists(), "directory/file2.html should exist")
    }

    @Test
    fun `creates files manually`() {

        val topDir = dir(nonExistingPath, fs4k, Manual)

        val plainFile = topDir.text("plainfile.txt") {
            content = "hello"
        }
        val bottomDir = topDir.dir("directory")

        val bottomFile = bottomDir.text("file2.html") {
            content = "<html/>"
        }

        assertFalse(nonExistingPath.toFile().exists(), "directory should not exist")
        topDir.create()
        assertTrue(nonExistingPath.toFile().exists(), "directory should exist")

        assertFalse(File(nonExistingPath.toFile(), "plainfile.txt").exists(), "plainfile.txt should not exist")
        plainFile.create()
        assertTrue(File(nonExistingPath.toFile(), "plainfile.txt").exists(), "plainfile.txt should exist")

        assertFalse(File(nonExistingPath.toFile(), "directory/file2.html").exists(), "file2.html should not exist")
        bottomFile.create()
        assertTrue(File(nonExistingPath.toFile(), "directory").exists(), "directory should exist")
        assertTrue(File(nonExistingPath.toFile(), "directory/file2.html").exists(), "file2.html should exist")
    }

    @Test
    fun `delete file after creation`() {
        val file = dir(nonExistingPath, fs4k, Manual).text("plainfile.txt")

        file.create()
        assertTrue(File(nonExistingPath.toFile(), "plainfile.txt").exists(), "plainfile.txt should exist")

        file.delete()
        assertFalse(File(nonExistingPath.toFile(), "plainfile.txt").exists(), "plainfile.txt should not exist")
    }

    @Test
    fun `delete directory deletes children`() {
        val dir = dir(nonExistingPath, fs4k)
        dir.text("plainfile.txt")

        assertTrue(File(nonExistingPath.toFile(), "plainfile.txt").exists(), "plainfile.txt should exist")

        dir.delete()

        assertFalse(nonExistingPath.toFile().exists(), "rootpath should not exist")
    }

    @Test
    fun `delete file from existing`() {
        val file = dir(existingParentPath, fs4k, Manual).text("plainfile.txt")

        file.create()
        assertTrue(File(existingParentPath.toFile(), "plainfile.txt").exists(), "plainfile.txt should exist")

        file.delete()
        assertFalse(File(existingParentPath.toFile(), "plainfile.txt").exists(), "plainfile.txt should not exist")
    }
}
