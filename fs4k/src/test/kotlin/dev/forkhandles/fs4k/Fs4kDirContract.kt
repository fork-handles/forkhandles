package dev.forkhandles.fs4k

import dev.forkhandles.fs4k.CreateMode.Automatic
import dev.forkhandles.fs4k.CreateMode.Manual
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

interface Fs4kDirContract {
    val fs4k: Fs4k

    val nonExistingPath: Fs4kPath
    val existingParentPath: Fs4kPath

    @Test
    fun `creates files automatically`() {
        val dir = dir(nonExistingPath, Automatic, fs4k) {
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

        assertTrue(dir.exists(nonExistingPath, "plainfile.txt"), "plainfile.txt should exist")
        assertEquals("hello", dir.content(nonExistingPath, "plainfile.txt"))

        assertTrue(dir.exists(nonExistingPath, "binary.png"), "binary.png should exist")
        assertEquals("goodbye", dir.content(nonExistingPath, "binary.png"))

        assertTrue(dir.exists(nonExistingPath, "directory"), "directory should exist")
        assertTrue(dir.exists(nonExistingPath, "directory/file2.html"), "directory/file2.html should exist")
    }

    @Test
    fun `creates files manually`() {

        val dir = dir(nonExistingPath, Manual, fs4k)

        val plainFile = dir.text("plainfile.txt") {
            content = "hello"
        }
        val bottomDir = dir.dir("directory")

        val bottomFile = bottomDir.text("file2.html") {
            content = "<html/>"
        }

        assertFalse(dir.exists(nonExistingPath), "directory should not exist")
        dir.create()
        assertTrue(dir.exists(nonExistingPath), "directory should exist")

        assertFalse(dir.exists(nonExistingPath, "plainfile.txt"), "plainfile.txt should not exist")
        plainFile.create()
        assertTrue(dir.exists(nonExistingPath, "plainfile.txt"), "plainfile.txt should exist")

        assertFalse(dir.exists(nonExistingPath, "directory/file2.html"), "file2.html should not exist")
        bottomFile.create()
        assertTrue(dir.exists(nonExistingPath, "directory"), "directory should exist")
        assertTrue(dir.exists(nonExistingPath, "directory/file2.html"), "file2.html should exist")
    }

    @Test
    fun `delete file after creation`() {
        val dir = dir(nonExistingPath, Manual, fs4k)
        val file = dir.text("plainfile.txt")

        file.create()
        assertTrue(dir.exists(nonExistingPath, "plainfile.txt"), "plainfile.txt should exist")

        file.delete()
        assertFalse(dir.exists(nonExistingPath, "plainfile.txt"), "plainfile.txt should not exist")
    }

    @Test
    fun `delete directory deletes children`() {
        val dir = dir(nonExistingPath, fs4k = fs4k)
        dir.text("plainfile.txt")

        assertTrue(dir.exists(nonExistingPath, "plainfile.txt"), "plainfile.txt should exist")

        dir.delete()

        assertFalse(dir.exists(nonExistingPath), "rootpath should not exist")
    }

    @Test
    fun `delete file from existing`() {
        val dir = dir(existingParentPath, Manual, fs4k)
        val file = dir.text("plainfile.txt")

        file.create()
        assertTrue(dir.exists(existingParentPath, "plainfile.txt"), "plainfile.txt should exist")

        file.delete()
        assertFalse(dir.exists(existingParentPath, "plainfile.txt"), "plainfile.txt should not exist")
    }

    fun Fs4kDir.exists(path: Fs4kPath, name: String): Boolean

    fun Fs4kDir.content(path: Fs4kPath, name: String): String

    fun Fs4kDir.exists(path: Fs4kPath): Boolean
}
