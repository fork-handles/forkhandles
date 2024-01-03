package dev.forkhandles.lens

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmErasure

abstract class AbstractLensProp<IN, OUT>(
    private val existsFn: IN.(String) -> Boolean,
    private val getFn: IN.(String) -> Any?
) : ReadOnlyProperty<IN, OUT> {
    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: IN, property: KProperty<*>): OUT {
        val result = thisRef.getFn(property.name)
        return when {
            result == null -> when {
                thisRef.existsFn(property.name) -> throw NoSuchElementException("Value for field <${property.name}> is null")
                else -> throw NoSuchElementException("Field <${property.name}> is missing")
            }
            (property.returnType.jvmErasure == result.javaClass.kotlin) -> result as OUT

            else -> throw NoSuchElementException("Value for field <${property.name}> is not a ${property.returnType.jvmErasure}")
        }
    }
}
