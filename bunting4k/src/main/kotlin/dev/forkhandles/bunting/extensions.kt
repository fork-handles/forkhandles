package dev.forkhandles.bunting

import kotlin.reflect.KProperty

fun KProperty<*>.typeDescription() = toString().drop(toString().lastIndexOf(".") + 1).uppercase()

