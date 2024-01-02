package dev.forkhandles.lens

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.message

class JacksonWrapperTest {

    class SubMap(node: JsonNode) : JacksonWrapper(node) {
        val stringField by Field<String>()
    }

    class NodeBacked(node: JsonNode) : JacksonWrapper(node) {
        val stringField by Field<String>()
        val booleanField by Field<Boolean>()
        val intField by Field<Int>()
        val longField by Field<Long>()
        val decimalField by Field<Double>()
        val notAStringField by Field<String>()
        val noSuchField by Field<String>()
        val listField by ListField(::SubMap)
        val listField2 by ListField(Any::toString)
        val objectField by ObjectField(::SubMap)
    }

    @Test
    fun `can get values from properties`() {
        val map = mapOf(
            "stringField" to "string",
            "booleanField" to true,
            "intField" to 123,
            "longField" to Long.MAX_VALUE,
            "decimalField" to 1.1234,
            "notAStringField" to 123,
            "listField" to listOf(
                mapOf("stringField" to "string1"),
                mapOf("stringField" to "string2"),
            ),
            "listField2" to listOf("string1", "string2"),
            "objectField" to mapOf(
                "stringField" to "string"
            )
        )

        val mapBacked = NodeBacked(
            ObjectMapper().valueToTree(
                map
            )
        )

        expectThat(mapBacked.stringField).isEqualTo("string")
        expectThat(mapBacked.booleanField).isEqualTo(true)
        expectThat(mapBacked.intField).isEqualTo(123)
        expectThat(mapBacked.longField).isEqualTo(Long.MAX_VALUE)
        expectThat(mapBacked.decimalField).isEqualTo(1.1234)
        expectThat(mapBacked.listField.map { it.stringField }).isEqualTo(listOf("string1", "string2"))
        expectThat(mapBacked.listField2).isEqualTo(listOf("string1", "string2"))
        expectThat(mapBacked.objectField.stringField).isEqualTo("string")
        expectThrows<NoSuchElementException> { mapBacked.notAStringField }.message.isEqualTo("Value for field <notAStringField> is not a class kotlin.String but class kotlin.Int")
        expectThrows<NoSuchElementException> { mapBacked.noSuchField }.message.isEqualTo("Field <noSuchField> is missing")
    }
}
