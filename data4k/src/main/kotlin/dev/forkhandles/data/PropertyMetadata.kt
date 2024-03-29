package dev.forkhandles.data

import kotlin.reflect.KType

/**
 * Represents all of the attached metadata for a single property
 */
data class PropertyMetadata(val name: String, val type: KType, val data: List<Metadatum>)
