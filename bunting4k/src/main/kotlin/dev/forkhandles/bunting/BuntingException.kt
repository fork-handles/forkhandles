package dev.forkhandles.bunting

import kotlin.reflect.KProperty

sealed class BuntingException(message: String, cause: Throwable? = null) : RuntimeException(message, cause)

class Help(message: String) : BuntingException(message)

class MissingFlag(property: KProperty<*>) : BuntingException("Missing --${property.name} (${property.typeDescription()}) flag. Use --help for docs.")

class IllegalFlag(property: KProperty<*>, value: String, cause: Throwable) :
    BuntingException("Illegal --${property.name} (${property.typeDescription()}) flag: $value. Use --help for docs.", cause)
