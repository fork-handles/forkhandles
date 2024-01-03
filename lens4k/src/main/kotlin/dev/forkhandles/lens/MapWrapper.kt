package dev.forkhandles.lens

@Suppress("UNCHECKED_CAST")
abstract class MapWrapper(private val map: Map<String, Any?>) {
    class Field<OUT> : AbstractLensProp<MapWrapper, OUT>({ map.containsKey(it) }, { map[it] })
    class ListField<IN : Any, OUT>(mapFn: (IN) -> OUT) : AbstractLensProp<MapWrapper, List<OUT>>(
        { map.containsKey(it) },
        { (map[it] as List<IN>).map(mapFn) }
    )

    class ObjectField<OUT : MapWrapper>(wrapper: (Map<String, Any?>) -> OUT) :
        AbstractLensProp<MapWrapper, OUT>(
            { map.containsKey(it) },
            { wrapper(map[it] as Map<String, Any?>) }
        )

}
