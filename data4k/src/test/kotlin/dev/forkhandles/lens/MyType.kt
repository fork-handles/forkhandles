package dev.forkhandles.lens

import dev.forkhandles.values.IntValue
import dev.forkhandles.values.IntValueFactory

class MyType private constructor(value: Int) : IntValue(value) {
    companion object : IntValueFactory<MyType>(::MyType)
}
