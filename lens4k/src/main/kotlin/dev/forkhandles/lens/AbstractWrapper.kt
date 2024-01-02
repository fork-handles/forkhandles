package dev.forkhandles.lens

@Suppress("UNCHECKED_CAST")
abstract class AbstractWrapper<CONTENT>(
    private val contents: CONTENT,
    existsFn: (CONTENT, String) -> Boolean,
    getFn: (CONTENT, String) -> Any?
) {
    private val exists: AbstractWrapper<CONTENT>.(String) -> Boolean = { existsFn(contents, it) }
    private val get: AbstractWrapper<CONTENT>.(String) -> Any? = { getFn(contents, it) }

    inner class Field<OUT> : LensProp<AbstractWrapper<CONTENT>, OUT>(exists, get)

    inner class ListField<IN : Any, OUT>(mapFn: (IN) -> OUT) :
        LensProp<AbstractWrapper<CONTENT>, List<OUT>>(exists, { (get(it) as List<IN>).map(mapFn) })

    inner class ObjectField<OUT : AbstractWrapper<CONTENT>>(mapFn: (CONTENT) -> OUT) :
        LensProp<AbstractWrapper<CONTENT>, OUT>(exists, { mapFn(get(it) as CONTENT) })
}
