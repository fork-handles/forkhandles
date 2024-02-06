package dev.forkhandles.data

/**
 * Map-based implementation of the DataContainer
 */
open class MapDataContainer(input: Map<String, Any?> = emptyMap()) :
    DataContainer<MutableMap<String, Any?>>(input.toMutableMap(), { content, it -> content.containsKey(it) },
        { content, it -> content[it] },
        { map, name, value -> map[name] = value }
    )
