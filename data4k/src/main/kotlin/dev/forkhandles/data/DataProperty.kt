package dev.forkhandles.data

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmErasure

open class DataProperty<IN, OUT : Any?>(
    private val existsFn: IN.(String) -> Boolean,
    private val getFn: IN.(String) -> Any?
) : ReadOnlyProperty<IN, OUT> {
    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: IN, property: KProperty<*>): OUT {
        val result = thisRef.getFn(property.name)

        return when {
            result == null -> when {
                property.returnType.isMarkedNullable -> null as OUT
                thisRef.existsFn(property.name) -> throw NoSuchElementException("Value for field <${property.name}> is null")
                else -> throw NoSuchElementException("Field <${property.name}> is missing")
            }
            property.returnType.jvmErasure.isInstance(result) -> result as OUT

            else -> throw NoSuchElementException("Value for field <${property.name}> is not a ${property.returnType.jvmErasure} but ${result.javaClass.kotlin}")
        }
    }
}

