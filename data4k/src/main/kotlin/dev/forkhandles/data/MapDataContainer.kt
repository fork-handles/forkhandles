package dev.forkhandles.data

import kotlin.collections.Map

/**
 * Map-based implementation of the DataContainer
 */
abstract class MapDataContainer(input: Map<String, Any?>) :
    DataContainer<Map<String, Any?>>(input, { content, it -> content.containsKey(it) }, { content, it -> content[it] })


