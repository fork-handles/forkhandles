package dev.forkhandles.data

import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

/**
 * Map-based implementation of the DataContainer
 */
open class MapDataContainer(input: Map<String, Any?> = emptyMap()) :
    DataContainer<MutableMap<String, Any?>>(input.toMutableMap(), { content, it -> content.containsKey(it) },
        { content, it -> content[it] },
        { map, name, value -> map[name] = value }
    )

enum class Meta : MetaProperty {
    foo
}

class MyThing : MapDataContainer() {
    val a by required<String,  String>({ it }, { it }, Meta.foo, Meta.foo)
}


fun main() {
    val a = MyThing()
    println(MyThing::class.memberProperties.map {
        it.isAccessible = true
        (it.getDelegate(a) as DataProperty<*, *>).meta
    })
}
