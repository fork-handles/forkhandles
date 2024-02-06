package dev.forkhandles.data

import dev.forkhandles.values.Value
import dev.forkhandles.values.ValueFactory

/**
 * Superclass for all container implementations. Defines the delegate property classes to extract data from the
 * underlying data structure.
 */
@Suppress("UNCHECKED_CAST")
abstract class DataContainer<CONTENT>(
    private val content: CONTENT,
    private val existsFn: (CONTENT, String) -> Boolean,
    private val getFn: (CONTENT, String) -> Any?,
    private val setFn: (CONTENT, String, Any?) -> Unit
) {
    /**
     * Expose the underlying data structure
     */
    fun content() = content

    /** Required **/

    protected fun <OUT : Any?, NEXT> required(
        mapInFn: (OUT) -> NEXT,
        mapOutFn: (NEXT) -> OUT?,
        vararg meta: MetaProperty
    ) = property<NEXT, OUT, OUT>(mapInFn, mapOutFn, *meta)

    protected fun <OUT, NEXT> required(mapInFn: (OUT) -> NEXT, vararg meta: MetaProperty) =
        required(mapInFn, { error("no outbound mapping defined") }, *meta)

    protected fun <OUT : Any> required(vararg meta: MetaProperty) = required<OUT, OUT>({ it }, { it }, *meta)

    protected fun <IN : Any, OUT : Value<IN>> required(factory: ValueFactory<OUT, IN>, vararg meta: MetaProperty) =
        required(factory::of, { it.value }, *meta)

    /** Optional **/

    protected fun <OUT, NEXT : Any> optional(
        mapInFn: (OUT) -> NEXT,
        mapOutFn: (NEXT) -> OUT?,
        vararg meta: MetaProperty
    ) =
        property<NEXT?, OUT, OUT>(mapInFn, { it?.let(mapOutFn) }, *meta)

    protected fun <OUT, NEXT : Any> optional(mapInFn: (OUT) -> NEXT, vararg meta: MetaProperty) =
        optional(mapInFn, { error("no outbound mapping defined") }, *meta)

    protected fun <OUT> optional(vararg meta: MetaProperty) = property<OUT?, OUT, OUT>({ it }, { it })

    protected fun <IN : Any, OUT : Value<IN>> optional(factory: ValueFactory<OUT, IN>, vararg meta: MetaProperty) =
        optional(factory::of, { it.value }, *meta)

    /** Object **/

    protected fun <OUT : DataContainer<CONTENT>> obj(
        mapInFn: (CONTENT) -> OUT,
        mapOutFn: (OUT) -> CONTENT?,
        vararg meta: MetaProperty
    ) =
        property<OUT, CONTENT, CONTENT>(mapInFn, mapOutFn, *meta)

    protected fun <OUT : DataContainer<CONTENT>> obj(mapInFn: (CONTENT) -> OUT, vararg meta: MetaProperty) =
        obj(mapInFn, { it.content }, *meta)

    protected fun <OUT : DataContainer<CONTENT>> optionalObj(mapInFn: (CONTENT) -> OUT, vararg meta: MetaProperty) =
        property<OUT?, CONTENT, CONTENT>(mapInFn, { it?.content }, *meta)

    /** List **/

    protected fun <OUT, IN> list(
        mapInFn: (IN) -> OUT, mapOutFn: (OUT) -> IN?,
        vararg meta: MetaProperty
    ) =
        property<List<OUT>, List<IN>, List<IN>>({ it.map(mapInFn) }, { it.mapNotNull(mapOutFn) }, *meta)

    protected fun <IN, OUT> list(mapInFn: (IN) -> OUT, vararg meta: MetaProperty) =
        list(mapInFn, { error("no outbound mapping defined") }, *meta)

    protected fun <OUT> list(vararg meta: MetaProperty) = list<OUT, OUT>({ it }, { it }, *meta)

    protected fun <IN : Any, OUT : Value<IN>> list(factory: ValueFactory<OUT, IN>, vararg meta: MetaProperty) =
        list(factory::of, { it.value }, *meta)

    @JvmName("listDataContainer")
    protected fun <OUT : DataContainer<CONTENT>?> list(mapInFn: (CONTENT) -> OUT, vararg meta: MetaProperty) =
        list(mapInFn, { it?.content }, *meta)

    protected fun <OUT, IN> optionalList(mapInFn: (IN) -> OUT, mapOutFn: (OUT) -> IN?, vararg meta: MetaProperty) =
        property<List<OUT>?, List<IN>, List<IN>>({ it.map(mapInFn) }, { it?.mapNotNull(mapOutFn) }, *meta)

    protected fun <OUT, IN> optionalList(mapInFn: (IN) -> OUT, vararg meta: MetaProperty) =
        optionalList(mapInFn, { error("no outbound mapping defined") }, *meta)

    protected fun <OUT> optionalList(vararg meta: MetaProperty) = optionalList<OUT, OUT & Any>({ it }, { it }, *meta)

    protected fun <IN : Any, OUT : Value<IN>> optionalList(factory: ValueFactory<OUT, IN>, vararg meta: MetaProperty) =
        optionalList(factory::of, { it.value }, *meta)

    @JvmName("optionalListDataContainer")
    protected fun <OUT : DataContainer<CONTENT>?> optionalList(mapInFn: (CONTENT) -> OUT, vararg meta: MetaProperty) =
        optionalList(mapInFn, { it?.content }, *meta)

    /** Utility **/

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataContainer<*>

        return content == other.content
    }

    override fun hashCode() = content?.hashCode() ?: 0

    override fun toString() = content.toString()

    private fun <IN, OUT : Any?, OUT2> property(
        mapInFn: (OUT) -> IN,
        mapOutFn: (IN) -> OUT2?,
        vararg meta: MetaProperty
    ) =
        DataProperty<DataContainer<CONTENT>, IN>(
            { existsFn(content, it) },
            { getFn(content, it)?.let { value -> value as OUT }?.let(mapInFn) },
            { name, value -> setFn(content, name, value?.let(mapOutFn)) },
            meta.toList(),
        )
}

