package dev.forkhandles.data

import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.jvmErasure

class DataProperty<IN, OUT : Any?>(
    private val existsFn: IN.(String) -> Boolean,
    private val getFn: IN.(String) -> OUT?,
    private val setFn: IN.(String, OUT?) -> Unit,
    val meta: List<MetaProperty>
) : ReadWriteProperty<IN, OUT> {

    @Suppress("UNCHECKED_CAST")
    override fun getValue(thisRef: IN, property: KProperty<*>): OUT {
        val result = thisRef.getFn(property.name)

        return when {
            result == null -> when {
                property.returnType.isMarkedNullable -> null as OUT
                thisRef.existsFn(property.name) -> throw NoSuchElementException("Value for field <${property.name}> is null")
                else -> throw NoSuchElementException("Field <${property.name}> is missing")
            }

            property.returnType.jvmErasure.isInstance(result) -> result

            else -> throw NoSuchElementException("Value for field <${property.name}> is not a ${property.returnType.jvmErasure} but ${result.javaClass.kotlin}")
        }
    }

    override fun setValue(thisRef: IN, property: KProperty<*>, value: OUT) = thisRef.setFn(property.name, value)
}

