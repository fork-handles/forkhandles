package dev.forkhandles.lens

abstract class MapWrapper(map: Map<String, Any?>) :
    AbstractWrapper<Map<String, Any?>>(map, { content, it -> content.containsKey(it) }, { content, it -> content[it] })


