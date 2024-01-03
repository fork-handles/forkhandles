package dev.forkhandles.data

/**
 * Superclass for all container implementations. Defines the delegate property classes to extract data from the
 * underlying data structure.
 */
@Suppress("UNCHECKED_CAST", "ClassName")
abstract class DataContainer<CONTENT>(
    protected val data: CONTENT,
    existsFn: (CONTENT, String) -> Boolean,
    getFn: (CONTENT, String) -> Any?
) {
    private val exists: DataContainer<CONTENT>.(String) -> Boolean = { existsFn(data, it) }
    private val get: DataContainer<CONTENT>.(String) -> Any? = { getFn(data, it) }

    inner class field<OUT> : DataProperty<DataContainer<CONTENT>, OUT>(exists, get)

    inner class list<IN : Any, OUT>(mapFn: (IN) -> OUT) :
        DataProperty<DataContainer<CONTENT>, List<OUT>>(exists, { (get(it) as List<IN>).map(mapFn) })

    inner class obj<OUT : DataContainer<CONTENT>>(mapFn: (CONTENT) -> OUT) :
        DataProperty<DataContainer<CONTENT>, OUT>(exists, { mapFn(get(it) as CONTENT) })
}
