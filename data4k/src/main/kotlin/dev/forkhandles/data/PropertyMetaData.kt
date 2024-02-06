package dev.forkhandles.data

import kotlin.reflect.KType

/**
 * Represents all of the attached metadata for a single property
 */
data class PropertyMetaData(val name: String, val type: KType, val data: List<MetaDatum>)
