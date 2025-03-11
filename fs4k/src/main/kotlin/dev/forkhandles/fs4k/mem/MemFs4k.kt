package dev.forkhandles.fs4k.mem

import dev.forkhandles.fs4k.CreateMode
import dev.forkhandles.fs4k.Fs4k
import dev.forkhandles.fs4k.Fs4kDir
import dev.forkhandles.fs4k.Fs4kPath
import dev.forkhandles.fs4k.mem.MemFs4k.SEPARATOR

/**
 * A filesystem that uses the in-memory implementation.
 */
object MemFs4k : Fs4k {
    internal const val SEPARATOR = "/"

    internal val state: MutableMap<Fs4kPath, String?> = mutableMapOf()

    override fun invoke(dir: String, createMode: CreateMode): Fs4kDir = Dir(state, Fs4kPath.of(dir), createMode)

    override fun invoke(path: Fs4kPath, createMode: CreateMode): Fs4kDir =
        Dir(state, Fs4kPath.of(path.value), createMode)
}


internal fun Fs4kPath.startsWith(other: Fs4kPath) = value.startsWith(other.value)

internal fun Fs4kPath.parentPaths() =
    value
        .split(SEPARATOR)
        .map(Fs4kPath::of)
        .fold(emptyList<Fs4kPath>()) { acc, it -> acc + (acc.lastOrNull()?.add(it) ?: it) }
        .filterNot { it.value == "" }

internal fun Fs4kPath.add(other: String) = Fs4kPath.of("$value/$other")

internal fun Fs4kPath.add(other: Fs4kPath) = Fs4kPath.of("$value/${other.value}")

internal fun MutableMap<Fs4kPath, String?>.ensureParents(path: Fs4kPath) {
    path.parentPaths().forEach { if (!containsKey(it)) this[it] = null }
}
