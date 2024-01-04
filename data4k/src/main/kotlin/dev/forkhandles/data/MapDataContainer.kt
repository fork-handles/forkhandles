package dev.forkhandles.data

/**
 * Map-based implementation of the DataContainer
 */
abstract class MapDataContainer(input: Map<String, Any?>) :
    DataContainer<MutableMap<String, Any?>>(input.toMutableMap(), { content, it -> content.containsKey(it) },
        { content, it -> content[it] },
        { map, name, value -> map[name] = value }
    )
