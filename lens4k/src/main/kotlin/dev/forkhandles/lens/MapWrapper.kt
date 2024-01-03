package dev.forkhandles.lens

abstract class MapWrapper(private val map: Map<String, Any?>) {
    class Field<OUT> : AbstractLensProp<MapWrapper, OUT>({ map.containsKey(it) }, { map[it] })
    class ListField<IN, OUT>(mapFn: (IN) -> OUT) : AbstractLensProp<MapWrapper, List<OUT>>({ map.containsKey(it) }, {
        @Suppress("UNCHECKED_CAST")
        (map[it] as List<IN>).map(mapFn)
    })
}
