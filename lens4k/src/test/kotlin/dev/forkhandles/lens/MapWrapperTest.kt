package dev.forkhandles.lens

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.message

class MapWrapperTest {

    class SubMap(propertySet: Map<String, Any?>) : MapWrapper(propertySet) {
        val stringField by Field<String>()
    }

    class MapBacked(propertySet: Map<String, Any?>) : MapWrapper(propertySet) {
        val stringField by Field<String>()
        val booleanField by Field<Boolean>()
        val intField by Field<Int>()
        val longField by Field<Long>()
        val decimalField by Field<Double>()
        val notAStringField by Field<String>()
        val noSuchField by Field<String>()
        val listField by ListField(::SubMap)
    }

    @Test
    fun `can get values from properties`() {
        val mapBacked = MapBacked(
            mapOf(
                "stringField" to "string",
                "booleanField" to true,
                "intField" to 123,
                "longField" to Long.MAX_VALUE,
                "decimalField" to 1.1234,
                "notAStringField" to 123,
                "listField" to listOf(
                    mapOf("stringField" to "string1"),
                    mapOf("stringField" to "string2"),
                )
            )
        )

        expectThat(mapBacked.stringField).isEqualTo("string")
        expectThat(mapBacked.booleanField).isEqualTo(true)
        expectThat(mapBacked.intField).isEqualTo(123)
        expectThat(mapBacked.longField).isEqualTo(Long.MAX_VALUE)
        expectThat(mapBacked.decimalField).isEqualTo(1.1234)
        expectThat(mapBacked.listField.map { it.stringField }).isEqualTo(listOf("string1", "string2"))
        expectThrows<NoSuchElementException> { mapBacked.notAStringField }.message.isEqualTo("Value for field <notAStringField> is not a class kotlin.String but class kotlin.Int")
        expectThrows<NoSuchElementException> { mapBacked.noSuchField }.message.isEqualTo("Field <noSuchField> is missing")
    }
}
