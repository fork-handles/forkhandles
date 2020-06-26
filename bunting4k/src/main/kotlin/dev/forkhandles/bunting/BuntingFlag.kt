package dev.forkhandles.bunting

import kotlin.properties.ReadOnlyProperty
import kotlin.reflect.KProperty

data class BuntingFlag<T> internal constructor(
    internal val fn: (String) -> T,
    val description: String? = null,
    internal val default: String?
) : ReadOnlyProperty<Bunting, T> {

    fun <NEXT> map(nextFn: (T) -> NEXT) = BuntingFlag({ nextFn(fn(it)) }, description, default)

    override fun getValue(thisRef: Bunting, property: KProperty<*>): T {
        val windowed = thisRef.args.toList().windowed(2).map { it[0] to it[1] }.toMap()

        val init = windowed["--${property.name}"] ?: windowed["-${property.name.first()}"] ?: default

        return init?.let {
            try {
                fn(it) ?: throw MissingFlag(property)
            } catch (e: Exception) {
                throw IllegalFlag(property, it, e)
            }
        } ?: throw MissingFlag(property)
    }
}