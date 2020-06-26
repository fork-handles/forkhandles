package dev.forkhandles.bunting

import java.lang.RuntimeException
import kotlin.reflect.KProperty

sealed class BuntingException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class Help(message: String) : BuntingException(message)

class MissingFlag(property: KProperty<*>) : BuntingException("Missing --${property.name} (${property.typeDescription()}) option")

class IllegalFlag(property: KProperty<*>, value: String, cause: Throwable) :
    BuntingException("Illegal --${property.name} (${property.typeDescription()}) option: $value", cause)
