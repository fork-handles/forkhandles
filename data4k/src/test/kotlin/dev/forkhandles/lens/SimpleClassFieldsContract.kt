package dev.forkhandles.lens

import dev.forkhandles.data.DataContainer
import dev.forkhandles.data.PropertyMetadata
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import kotlin.reflect.full.starProjectedType

interface SimpleClassFieldsContract {
    fun container(input: Map<String, Any?>): SimpleClassFields

    @Test
    fun `get meta data from the container`() {
        val input = container(emptyMap()) as DataContainer<*>

        val propertyMetaData = input.propertyMetadata().find { it.name == "string" }
        expectThat(propertyMetaData).isEqualTo(
            PropertyMetadata(
                "string",
                String::class.starProjectedType,
                listOf(ContainerMeta.foo, ContainerMeta.bar)
            )
        )
    }
}
