package dev.forkhandles.data

import dev.forkhandles.values.Value
import dev.forkhandles.values.ValueFactory
import kotlin.reflect.KClass
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.isAccessible

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
     * Retrieve the attached metadata data about each of the properties
     */
    fun propertyMetadata(): List<PropertyMetadata> = kClass().memberProperties
        .mapNotNull { prop ->
            prop.isAccessible = true
            val delegate = prop.getDelegate(this)
            when {
                delegate is DataProperty<*, *> -> PropertyMetadata(prop.name, prop.returnType, delegate.data)
                else -> null
            }
        }

    /**
     * Expose the underlying data structure
     */
    fun content() = content

    /** Required **/

    protected fun <OUT : Any?, NEXT> required(
        mapInFn: (OUT) -> NEXT,
        mapOutFn: (NEXT) -> OUT?,
        vararg metaData: Metadatum
    ) = property<NEXT, OUT, OUT>(mapInFn, mapOutFn, *metaData)

    protected fun <OUT, NEXT> required(mapInFn: (OUT) -> NEXT, vararg metaData: Metadatum) =
        required(mapInFn, { error("no outbound mapping defined") }, *metaData)

    protected fun <OUT : Any> required(vararg metaData: Metadatum) = required<OUT, OUT>({ it }, { it }, *metaData)

    protected fun <IN : Any, OUT : Value<IN>> required(factory: ValueFactory<OUT, IN>, vararg metaData: Metadatum) =
        required(factory::of, { it.value }, *metaData)

    /** Optional **/

    protected fun <OUT, NEXT : Any> optional(
        mapInFn: (OUT) -> NEXT,
        mapOutFn: (NEXT) -> OUT?,
        vararg metaData: Metadatum
    ) =
        property<NEXT?, OUT, OUT>(mapInFn, { it?.let(mapOutFn) }, *metaData)

    protected fun <OUT, NEXT : Any> optional(mapInFn: (OUT) -> NEXT, vararg metaData: Metadatum) =
        optional(mapInFn, { error("no outbound mapping defined") }, *metaData)

    protected fun <OUT> optional(vararg metaData: Metadatum) = property<OUT?, OUT, OUT>({ it }, { it }, *metaData)

    protected fun <IN : Any, OUT : Value<IN>> optional(factory: ValueFactory<OUT, IN>, vararg metaData: Metadatum) =
        optional(factory::of, { it.value }, *metaData)

    /** Object **/

    protected fun <OUT : DataContainer<CONTENT>> obj(
        mapInFn: (CONTENT) -> OUT,
        mapOutFn: (OUT) -> CONTENT?,
        vararg metaData: Metadatum
    ) =
        property<OUT, CONTENT, CONTENT>(mapInFn, mapOutFn, *metaData)

    protected fun <OUT : DataContainer<CONTENT>> obj(mapInFn: (CONTENT) -> OUT, vararg metaData: Metadatum) =
        obj(mapInFn, { it.content }, *metaData)

    protected fun <OUT : DataContainer<CONTENT>> optionalObj(mapInFn: (CONTENT) -> OUT, vararg metaData: Metadatum) =
        property<OUT?, CONTENT, CONTENT>(mapInFn, { it?.content }, *metaData)

    /** List **/

    protected fun <OUT, IN> list(
        mapInFn: (IN) -> OUT, mapOutFn: (OUT) -> IN?,
        vararg metaData: Metadatum
    ) =
        property<List<OUT>, List<IN>, List<IN>>({ it.map(mapInFn) }, { it.mapNotNull(mapOutFn) }, *metaData)

    protected fun <IN, OUT> list(mapInFn: (IN) -> OUT, vararg metaData: Metadatum) =
        list(mapInFn, { error("no outbound mapping defined") }, *metaData)

    protected fun <OUT> list(vararg metaData: Metadatum) = list<OUT, OUT>({ it }, { it }, *metaData)

    protected fun <IN : Any, OUT : Value<IN>> list(factory: ValueFactory<OUT, IN>, vararg metaData: Metadatum) =
        list(factory::of, { it.value }, *metaData)

    @JvmName("listDataContainer")
    protected fun <OUT : DataContainer<CONTENT>?> list(mapInFn: (CONTENT) -> OUT, vararg metaData: Metadatum) =
        list(mapInFn, { it?.content }, *metaData)

    protected fun <OUT, IN> optionalList(mapInFn: (IN) -> OUT, mapOutFn: (OUT) -> IN?, vararg metaData: Metadatum) =
        property<List<OUT>?, List<IN>, List<IN>>({ it.map(mapInFn) }, { it?.mapNotNull(mapOutFn) }, *metaData)

    protected fun <OUT, IN> optionalList(mapInFn: (IN) -> OUT, vararg metaData: Metadatum) =
        optionalList(mapInFn, { error("no outbound mapping defined") }, *metaData)

    protected fun <OUT> optionalList(vararg metaData: Metadatum) =
        optionalList<OUT, OUT & Any>({ it }, { it }, *metaData)

    protected fun <IN : Any, OUT : Value<IN>> optionalList(factory: ValueFactory<OUT, IN>, vararg metaData: Metadatum) =
        optionalList(factory::of, { it.value }, *metaData)

    @JvmName("optionalListDataContainer")
    protected fun <OUT : DataContainer<CONTENT>?> optionalList(mapInFn: (CONTENT) -> OUT, vararg metaData: Metadatum) =
        optionalList(mapInFn, { it?.content }, *metaData)

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
        vararg metaData: Metadatum
    ) =
        DataProperty<DataContainer<CONTENT>, IN>(
            { existsFn(content, it) },
            { getFn(content, it)?.let { value -> value as OUT }?.let(mapInFn) },
            { name, value -> setFn(content, name, value?.let(mapOutFn)) },
            metaData.toList(),
        )

    private fun Any.kClass() = this::class as KClass<DataContainer<CONTENT>>
}

