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
abstract class DataContainer<DATA>(
    private val data: DATA,
    private val existsFn: (DATA, String) -> Boolean,
    private val getFn: (DATA, String) -> Any?,
    private val setFn: (DATA, String, Any?) -> Unit
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
     * Expose the backing data structure
     */
    fun unwrap() = data

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

    protected fun <OUT : DataContainer<DATA>> obj(
        mapInFn: (DATA) -> OUT,
        mapOutFn: (OUT) -> DATA?,
        vararg metaData: Metadatum
    ) = property<OUT, DATA, DATA>(mapInFn, mapOutFn, *metaData)

    protected fun <OUT : DataContainer<DATA>> obj(mapInFn: (DATA) -> OUT, vararg metaData: Metadatum) =
        obj(mapInFn, { it.unwrap() }, *metaData)

    protected fun <OUT : DataContainer<DATA>> optionalObj(mapInFn: (DATA) -> OUT, vararg metaData: Metadatum) =
        property<OUT?, DATA, DATA>(mapInFn, { it?.unwrap() }, *metaData)

    /** Data **/

    protected fun requiredData(vararg metaData: Metadatum) =
        property<DATA, DATA, DATA>({ it }, { it }, *metaData)

    protected fun optionalData(vararg metaData: Metadatum) =
        property<DATA?, DATA, DATA>({ it }, { it }, *metaData)

    /** List **/

    protected fun <OUT, IN> requiredList(
        mapInFn: (IN) -> OUT, mapOutFn: (OUT) -> IN?,
        vararg metaData: Metadatum
    ) =
        property<List<OUT>, List<IN>, List<IN>>({ it.map(mapInFn) }, { it.mapNotNull(mapOutFn) }, *metaData)

    protected fun <IN, OUT> requiredList(mapInFn: (IN) -> OUT, vararg metaData: Metadatum) =
        requiredList(mapInFn, { error("no outbound mapping defined") }, *metaData)

    protected fun <OUT> requiredList(vararg metaData: Metadatum) = requiredList<OUT, OUT>({ it }, { it }, *metaData)

    protected fun <IN : Any, OUT : Value<IN>> requiredList(factory: ValueFactory<OUT, IN>, vararg metaData: Metadatum) =
        requiredList(factory::of, { it.value }, *metaData)

    @JvmName("listDataContainer")
    protected fun <OUT : DataContainer<DATA>?> requiredList(mapInFn: (DATA) -> OUT, vararg metaData: Metadatum) =
        requiredList(mapInFn, { it?.unwrap() }, *metaData)

    protected fun <OUT, IN> optionalList(mapInFn: (IN) -> OUT, mapOutFn: (OUT) -> IN?, vararg metaData: Metadatum) =
        property<List<OUT>?, List<IN>, List<IN>>({ it.map(mapInFn) }, { it?.mapNotNull(mapOutFn) }, *metaData)

    protected fun <OUT, IN> optionalList(mapInFn: (IN) -> OUT, vararg metaData: Metadatum) =
        optionalList(mapInFn, { error("no outbound mapping defined") }, *metaData)

    protected fun <OUT> optionalList(vararg metaData: Metadatum) =
        optionalList<OUT, OUT & Any>({ it }, { it }, *metaData)

    protected fun <IN : Any, OUT : Value<IN>> optionalList(factory: ValueFactory<OUT, IN>, vararg metaData: Metadatum) =
        optionalList(factory::of, { it.value }, *metaData)

    @JvmName("optionalListDataContainer")
    protected fun <OUT : DataContainer<DATA>?> optionalList(mapInFn: (DATA) -> OUT, vararg metaData: Metadatum) =
        optionalList(mapInFn, { it?.unwrap() }, *metaData)

    /** Utility **/

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as DataContainer<*>

        return unwrap() == other.unwrap()
    }

    override fun hashCode() = unwrap()?.hashCode() ?: 0

    override fun toString() = unwrap().toString()

    private fun <IN, OUT : Any?, NEXT> property(
        mapInFn: (OUT) -> IN,
        mapOutFn: (IN) -> NEXT?,
        vararg metaData: Metadatum
    ) = DataProperty<DataContainer<DATA>, IN>(
        { name -> existsFn(unwrap(), name) },
        { name -> getFn(unwrap(), name)?.let { value -> value as OUT }?.let(mapInFn) },
        { name, value -> setFn(unwrap(), name, value?.let(mapOutFn)) },
        metaData.toList(),
    )

    private fun Any.kClass() = this::class as KClass<DataContainer<DATA>>

    /** Deprecated **/
    @Deprecated("renamed", ReplaceWith("requiredData(*metaData)"))
    protected fun data(vararg metaData: Metadatum) = requiredData( *metaData)

    @Deprecated("renamed", ReplaceWith("requiredList(mapInFn, mapOutFn, *metaData)"))
    protected fun <OUT, IN> list(
        mapInFn: (IN) -> OUT, mapOutFn: (OUT) -> IN?,
        vararg metaData: Metadatum
    ) = requiredList(mapInFn, mapOutFn, *metaData)

    @Deprecated("renamed", ReplaceWith("requiredList(mapInFn, *metaData)"))
    protected fun <IN, OUT> list(mapInFn: (IN) -> OUT, vararg metaData: Metadatum) =
        requiredList(mapInFn, *metaData)

    @Deprecated("renamed", ReplaceWith("requiredList(*metaData)"))
    protected fun <OUT> list(vararg metaData: Metadatum) = requiredList<OUT>(*metaData)

    @Deprecated("renamed", ReplaceWith("requiredList(mapInFn, mapOutFn, *metaData)"))
    protected fun <IN : Any, OUT : Value<IN>> list(factory: ValueFactory<OUT, IN>, vararg metaData: Metadatum) =
        requiredList(factory, *metaData)

    @JvmName("listDataContainerDeprecated")
    @Deprecated("renamed", ReplaceWith("requiredList(mapInFn, mapOutFn, *metaData)"))
    protected fun <OUT : DataContainer<DATA>?> list(mapInFn: (DATA) -> OUT, vararg metaData: Metadatum) =
        requiredList(mapInFn,  *metaData)
}

