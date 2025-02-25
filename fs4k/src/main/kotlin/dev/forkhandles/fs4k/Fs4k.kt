package dev.forkhandles.fs4k

import dev.forkhandles.fs4k.CreateMode.Automatic
import java.nio.file.Path

/**
 * Represents a filesystem.
 */
interface Fs4k {

    /**
     * Factory for a root filesystem
     */
    operator fun invoke(dir: String, createMode: CreateMode = Automatic): Fs4kDir

    /**
     * Factory for a root filesystem
     */
    operator fun invoke(path: Path, createMode: CreateMode = Automatic): Fs4kDir
}
