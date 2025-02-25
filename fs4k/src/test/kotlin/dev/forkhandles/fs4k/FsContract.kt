package dev.forkhandles.fs4k

import dev.forkhandles.fs4k.CreateMode.Automatic
import dev.forkhandles.fs4k.CreateMode.Manual
import dev.forkhandles.fs4k.Fs.Companion.dir
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import java.io.File

interface FsContract {
    val rootPath: String

    @Test
    fun `creates files automatically`() {
        val newRoot = File(rootPath, "foo").path
        dir(newRoot, createMode = Automatic) {
            file("plainfile.txt") {
                content = "hello"
            }

            dir("directory") {
                file("file2.html") {
                    content = "<html/>"
                }
            }
        }

        assertTrue(File(newRoot, "plainfile.txt").exists(), "plainfile.txt should exist")
        assertTrue(File(newRoot, "directory").exists(), "directory should exist")
        assertTrue(File(newRoot, "directory/file2.html").exists(), "directory/file2.html should exist")
    }

    @Test
    fun `creates files manually`() {
        val newRoot = File(rootPath, "foo").path

        val fs4k = dir(newRoot, createMode = Manual) {
            file("plainfile.txt") {
                content = "hello"
            }

            dir("directory") {
                file("file2.html") {
                    content = "<html/>"
                }
            }
        }

        assertFalse(File(newRoot, "directory").exists(), "directory should exist")

        fs4k.create()

        assertTrue(File(newRoot, "plainfile.txt").exists(), "plainfile.txt should exist")
        assertTrue(File(newRoot, "directory").exists(), "directory should exist")
        assertTrue(File(newRoot, "directory/file2.html").exists(), "directory/file2.html should exist")
    }
}
