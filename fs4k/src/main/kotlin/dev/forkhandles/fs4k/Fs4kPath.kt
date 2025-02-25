package dev.forkhandles.fs4k

import dev.forkhandles.values.StringValue
import dev.forkhandles.values.StringValueFactory

class Fs4kPath(value: String) : StringValue(value) {
    companion object : StringValueFactory<Fs4kPath>(::Fs4kPath)
}
