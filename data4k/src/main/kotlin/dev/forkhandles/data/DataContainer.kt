package dev.forkhandles.data

import dev.forkhandles.values.Value
import dev.forkhandles.values.ValueFactory

/**
 * Superclass for all container implementations. Defines the delegate property classes to extract data from the
 * underlying data structure.
 */
@Suppress("UNCHECKED_CAST")
abstract class DataContainer<CONTENT>(
    val data: CONTENT,
    private val existsFn: (CONTENT, String) -> Boolean,
    private val getFn: (CONTENT, String) -> Any?,
    private val setFn: (CONTENT, String, Any?) -> Unit
) {
    /** Required **/

    fun <OUT : Any?, NEXT> required(mapInFn: (OUT) -> NEXT, mapOutFn: (NEXT) -> OUT?) =
        property<NEXT, OUT, OUT>(mapInFn, mapOutFn)

    fun <OUT : Any> required() = required<OUT, OUT>({ it }, { it })

    fun <OUT, NEXT> required(mapInFn: (OUT) -> NEXT) = required(mapInFn) { error("no outbound mapping defined") }

    fun <IN : Any, OUT : Value<IN>> required(factory: ValueFactory<OUT, IN>) =
        required(factory::of) { it.value }

    /** Optional **/

    fun <OUT, NEXT : Any> optional(mapInFn: (OUT) -> NEXT) =
        required<OUT, NEXT>(mapInFn) { error("no outbound mapping defined") }

    fun <OUT, NEXT : Any> optional(mapInFn: (OUT) -> NEXT, mapOutFn: (NEXT) -> OUT?) =
        required<OUT, NEXT?>(mapInFn) { it?.let(mapOutFn) }

    fun <OUT> optional() = required<OUT, OUT?>({ it }, { it })

    fun <IN : Any, OUT : Value<IN>> optional(factory: ValueFactory<OUT, IN>): DataProperty<DataContainer<CONTENT>, OUT?> =
        required(factory::of) { it?.value }

    /** Object **/

    fun <OUT : DataContainer<CONTENT>> obj(mapInFn: (CONTENT) -> OUT, mapOutFn: (OUT) -> CONTENT?) =
        property<OUT, CONTENT, CONTENT>(mapInFn, mapOutFn)

    fun <OUT : DataContainer<CONTENT>> obj(mapInFn: (CONTENT) -> OUT) =
        obj(mapInFn) { it.data }

    fun <OUT : DataContainer<CONTENT>> optionalObj(mapInFn: (CONTENT) -> OUT): DataProperty<DataContainer<CONTENT>, OUT?> =
        property<OUT?, CONTENT, CONTENT>(mapInFn) { it?.data }

    /** List **/

    fun <OUT, IN> list(mapInFn: (IN) -> OUT, mapOutFn: (OUT) -> IN?) =
        property<List<OUT>, List<IN>, List<IN>>({ it.map(mapInFn) }, { it.mapNotNull(mapOutFn) })

    fun <IN, OUT> list(mapInFn: (IN) -> OUT) = list(mapInFn) { error("no outbound mapping defined") }

    fun <OUT> list() = list<OUT, OUT>({ it }, { it })

    fun <IN : Any, OUT : Value<IN>> list(factory: ValueFactory<OUT, IN>) = list(factory::of) { it.value }

    @JvmName("listDataContainer")
    fun <OUT : DataContainer<CONTENT>?> list(mapInFn: (CONTENT) -> OUT) = list(mapInFn) { it?.data }

    fun <OUT, IN> optionalList(mapInFn: (IN) -> OUT, mapOutFn: (OUT) -> IN?) =
        property<List<OUT>?, List<IN>, List<IN>>({ it.map(mapInFn) }, { it?.mapNotNull(mapOutFn) })

    fun <OUT, IN> optionalList(mapInFn: (IN) -> OUT) =
        optionalList(mapInFn) { error("no outbound mapping defined") }

    fun <OUT> optionalList() = optionalList<OUT, OUT & Any>({ it }, { it })

    fun <IN : Any, OUT : Value<IN>> optionalList(factory: ValueFactory<OUT, IN>) = optionalList(factory::of) { it.value }

    @JvmName("optionalListDataContainer")
    fun <OUT : DataContainer<CONTENT>?> optionalList(mapInFn: (CONTENT) -> OUT) = optionalList(mapInFn) { it?.data }

    /** Utility **/

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataContainer<*>

        return data == other.data
    }

    override fun hashCode() = data?.hashCode() ?: 0

    override fun toString() = data.toString()

    private fun <IN, OUT : Any?, OUT2> property(mapInFn: (OUT) -> IN, mapOutFn: (IN) -> OUT2?) =
        DataProperty<DataContainer<CONTENT>, IN>(
            { existsFn(data, it) },
            { getFn(data, it)?.let { value -> value as OUT }?.let(mapInFn) },
            { name, value -> setFn(data, name, (value as IN?)?.let(mapOutFn)) })
}
