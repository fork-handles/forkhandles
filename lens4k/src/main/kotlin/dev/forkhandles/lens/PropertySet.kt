package dev.forkhandles.lens

typealias PropertySet = Map<String, Any?>

inline fun <reified T> PropertySet.valueOf(key: String): T =
    when (val result: Any? = get(key)) {
        is T -> result
        null -> {
            when {
                containsKey(key) -> throw NoSuchElementException("Value for key <$key> is null")
                else -> throw NoSuchElementException("Key <$key> is missing in the map")
            }
        }

        else -> throw NoSuchElementException("Value for key <$key> is not a ${T::class}")
    }

object PropertySets {
    @JvmName("lensPropertySet")
    fun lens(propertyName: String) = lens<PropertySet>(propertyName)

    @JvmName("asLensPropertySet")
    fun String.asLens() = lens(this)

    inline fun <reified OUT> String.asLens() = lens<OUT>(this)

    inline fun <reified OUT> lens(propertyName: String) =
        LensObject<PropertySet, OUT>(
            getter = { it.valueOf<OUT>(propertyName) },
            injector = { subject, value -> subject.toMutableMap().apply { this[propertyName] = value } }
        )
}

abstract class MapWrapper(private val map: Map<String, Any?>) {
    class Primitive<OUT> : AbstractLensProp<MapWrapper, OUT>({ map.containsKey(it) }, { map[it] })
}
