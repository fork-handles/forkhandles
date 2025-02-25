package dev.forkhandles.fs4k

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import java.io.File

interface FsContract {
    val rootPath: String

   @Test
   fun `creates files automatically`() {
       Fs.dir(File(rootPath, "foo").path, createMode = CreateMode.Automatic) {
           file("plainfile.txt") {
               content = "hello"
           }

           dir("directory") {
               file("file2.html") {
                   content = "<html/>"
               }
           }
       }

       Assertions.assertTrue(File(rootPath, "plainfile.txt").exists(), "plainfile.txt should exist")
       Assertions.assertTrue(File(rootPath, "directory").exists(), "directory should exist")
       Assertions.assertTrue(File(rootPath, "directory/file2.html").exists(), "directory/file2.html should exist")
   }

   @Test
   fun `creates files manually`() {
       val fs4k = Fs.dir(rootPath, createMode = CreateMode.Manual) {
           file("plainfile.txt") {
               content = "hello"
           }

           dir("directory") {
               file("file2.html") {
                   content = "<html/>"
               }
           }
       }

       Assertions.assertFalse(File(rootPath, "directory").exists(), "directory should exist")

       fs4k.create()

       Assertions.assertTrue(File(rootPath, "plainfile.txt").exists(), "plainfile.txt should exist")
       Assertions.assertTrue(File(rootPath, "directory").exists(), "directory should exist")
       Assertions.assertTrue(File(rootPath, "directory/file2.html").exists(), "directory/file2.html should exist")
   }
}
