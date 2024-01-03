package dev.forkhandles.data

import dev.forkhandles.values.Value
import dev.forkhandles.values.ValueFactory

/**
 * Superclass for all container implementations. Defines the delegate property classes to extract data from the
 * underlying data structure.
 */
@Suppress("UNCHECKED_CAST")
abstract class DataContainer<CONTENT>(
    protected val data: CONTENT,
    existsFn: (CONTENT, String) -> Boolean,
    getFn: (CONTENT, String) -> Any?
) {
    private val exists: DataContainer<CONTENT>.(String) -> Boolean = { existsFn(data, it) }
    private val get: DataContainer<CONTENT>.(String) -> Any? = { getFn(data, it) }

    fun <OUT> field() = DataProperty<DataContainer<CONTENT>, OUT>(exists, get)

    fun <OUT, NEXT> field(mapFn: (OUT) -> NEXT) = DataProperty<DataContainer<CONTENT>, NEXT>(exists) {
        (get(it) as OUT).let(mapFn)
    }

    fun <IN : Any, OUT : Value<IN>> field(factory: ValueFactory<OUT, IN>) =
        DataProperty<DataContainer<CONTENT>, OUT>(exists) { (get(it) as IN).let(factory::of) }

    fun <IN, OUT> list(mapFn: (IN) -> OUT) =
        DataProperty<DataContainer<CONTENT>, List<OUT>>(exists) { (get(it) as List<IN>).map(mapFn) }

    fun <IN : Any, OUT : Value<IN>> list(factory: ValueFactory<OUT, IN>) =
        DataProperty<DataContainer<CONTENT>, List<OUT>>(exists) { (get(it) as List<IN>).map(factory::of) }

    fun <OUT> list() = list<OUT, OUT> { it }

    fun <OUT : DataContainer<CONTENT>> obj(mapFn: (CONTENT) -> OUT) =
        DataProperty<DataContainer<CONTENT>, OUT>(exists) { mapFn(get(it) as CONTENT) }

    fun obj() = DataProperty<DataContainer<CONTENT>, CONTENT>(exists) { get(it) as CONTENT }
}
