package dev.forkhandles.lens

import com.fasterxml.jackson.databind.JsonNode

@Suppress("UNCHECKED_CAST")
abstract class AbstractWrapper<CONTENT>(
    private val contents: CONTENT,
    existsFn: CONTENT.(String) -> Boolean,
    getFn: CONTENT.(String) -> Any?
) {
    private val exists: AbstractWrapper<CONTENT>.(String) -> Boolean = { contents.existsFn(it) }
    private val get: AbstractWrapper<CONTENT>.(String) -> Any? = { contents.getFn(it) }

    inner class Field<OUT> : LensProp<AbstractWrapper<CONTENT>, OUT>(exists, get)

    inner class ListField<IN : Any, OUT>(mapFn: (IN) -> OUT) :
        LensProp<AbstractWrapper<CONTENT>, List<OUT>>(exists, { (get(it) as List<IN>).map(mapFn) })

    inner class ObjectField<OUT : AbstractWrapper<CONTENT>>(mapFn: (CONTENT) -> OUT) :
        LensProp<AbstractWrapper<CONTENT>, OUT>(exists, { mapFn(get(it) as CONTENT) })
}

abstract class MapWrapper(map: Map<String, Any?>) :
    AbstractWrapper<Map<String, Any?>>(map, { map.containsKey(it) }, { map[it] })

abstract class JacksonWrapper(node: JsonNode) :
    AbstractWrapper<JsonNode>(node, { node.has(it) }, { node[it] })
