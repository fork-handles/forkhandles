package dev.forkhandles.lens

import dev.forkhandles.data.DataContainer
import dev.forkhandles.data.PropertyMetadata
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.isNull
import strikt.assertions.message
import kotlin.reflect.full.starProjectedType

interface SimpleClassFieldsContract {
    fun container(input: Map<String, Any?>): SimpleClassFields

    @Test
    fun `can read simple values`() {
        val input = container(
            mapOf(
                "string" to "string",
                "mapped" to "123",
                "optional" to "optional",
            )
        )

        expectThat(input.standardField).isEqualTo("foobar")

        expectThat(input.string).isEqualTo("string")
        expectThrows<NoSuchElementException> { container(mapOf()).string }.message.isEqualTo("Field <string> is missing")

        expectThat(input.optional).isEqualTo("optional")
        expectThat(container(mapOf()).optional).isNull()
    }

    @Test
    fun `can write simple values`() {
        val input = container(
            mapOf(
                "string" to "string",
                "value" to 123,
                "mapped" to "123",

                "optionalValue" to 123,
                "optional" to "optional",
            )
        )

        expectSetWorks(input::standardField, "123")
        expectSetWorks(input::string, "123")

        expectSetWorks(input::optional, "123123")
        expectSetWorks(input::optional, null)
    }

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
