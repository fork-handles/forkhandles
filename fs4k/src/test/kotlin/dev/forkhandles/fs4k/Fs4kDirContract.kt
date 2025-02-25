package dev.forkhandles.fs4k

import dev.forkhandles.fs4k.CreateMode.Automatic
import dev.forkhandles.fs4k.CreateMode.Manual
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

interface Fs4kDirContract {
    val fs4k: Fs4k

    val nonExistingPath: Fs4kPath
    val existingParentPath: Fs4kPath

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

        assertTrue(exists(nonExistingPath, "directory"), "directory should exist")
        assertTrue(exists(nonExistingPath, "directory/file2.html"), "directory/file2.html should exist")
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

        assertFalse(exists(nonExistingPath, "plainfile.txt"), "plainfile.txt should not exist")
        plainFile.create()
        assertTrue(exists(nonExistingPath, "plainfile.txt"), "plainfile.txt should exist")

        assertFalse(exists(nonExistingPath, "directory/file2.html"), "file2.html should not exist")
        bottomFile.create()
        assertTrue(exists(nonExistingPath, "directory"), "directory should exist")
        assertTrue(exists(nonExistingPath, "directory/file2.html"), "file2.html should exist")
    }

    @Test
    fun `delete file after creation`() {
        val file = dir(nonExistingPath, fs4k, Manual).text("plainfile.txt")

        file.create()
        assertTrue(exists(nonExistingPath, "plainfile.txt"), "plainfile.txt should exist")

        file.delete()
        assertFalse(exists(nonExistingPath, "plainfile.txt"), "plainfile.txt should not exist")
    }

    @Test
    fun `delete directory deletes children`() {
        val dir = dir(nonExistingPath, fs4k)
        dir.text("plainfile.txt")

        assertTrue(exists(nonExistingPath, "plainfile.txt"), "plainfile.txt should exist")

        dir.delete()

        assertFalse(nonExistingPath.toFile().exists(), "rootpath should not exist")
    }

    @Test
    fun `delete file from existing`() {
        val file = dir(existingParentPath, fs4k, Manual).text("plainfile.txt")

        file.create()
        assertTrue(exists(existingParentPath, "plainfile.txt"), "plainfile.txt should exist")

        file.delete()
        assertFalse(exists(existingParentPath, "plainfile.txt"), "plainfile.txt should not exist")
    }

    fun exists(path: Fs4kPath, name: String): Boolean

    fun Fs4kPath.toFile() = File(toString())
}
