package dev.forkhandles.fs4k

import dev.forkhandles.fs4k.CreateMode.Automatic
import dev.forkhandles.fs4k.disk.DiskFs4k

/**
 * Create a directory in the filesystem.
 */
fun dir(
    path: Fs4kPath,
    createMode: CreateMode = Automatic,
    fs4k: Fs4k = DiskFs4k,
    fn: Fs4kDir.() -> Unit = {}
): Fs4kDir = fs4k(path, createMode).apply(fn)

/**
 * Create a directory in the filesystem.
 */
fun dir(
    path: String = ".",
    createMode: CreateMode = Automatic,
    fs4k: Fs4k = DiskFs4k,
    fn: Fs4kDir.() -> Unit = {}
) = dir(Fs4kPath.of(path), createMode, fs4k, fn)
