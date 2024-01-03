package dev.forkhandles.lens

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.message

interface MainClassFields {
    val stringField: String
    val booleanField: Boolean
    val intField: Int
    val longField: Long
    val decimalField: Double
    val notAStringField: String
    val optionalField: String?
    val noSuchField: String
    val listSubClassField: List<SubClassFields>
    val listIntsField: List<Int>
    val listStringsField: List<String>
    val objectField: SubClassFields
}

interface SubClassFields {
    val stringField: String
    val noSuchField: String
}

abstract class DataContainerContract {

    abstract fun container(input: Map<String, Any?>): MainClassFields

    @Test
    fun `can get values from properties`() {
        val input = container(
            mapOf(
                "stringField" to "string",
                "optionalField" to "optional",
                "booleanField" to true,
                "intField" to 123,
                "longField" to Long.MAX_VALUE,
                "decimalField" to 1.1234,
                "notAStringField" to 123,
                "listIntsField" to listOf(1, 2, 3),
                "listSubClassField" to listOf(
                    mapOf("stringField" to "string1"),
                    mapOf("stringField" to "string2"),
                ),
                "listStringsField" to listOf("string1", "string2"),
                "objectField" to mapOf(
                    "stringField" to "string"
                )
            )
        )

        expectThat(input.stringField).isEqualTo("string")
        expectThat(input.optionalField).isEqualTo("optional")
        expectThat(input.booleanField).isEqualTo(true)
        expectThat(input.intField).isEqualTo(123)
        expectThat(input.longField).isEqualTo(Long.MAX_VALUE)
        expectThat(input.decimalField).isEqualTo(1.1234)
        expectThat(input.listIntsField).isEqualTo(listOf(1, 2, 3))
        expectThat(input.listSubClassField.map { it.stringField }).isEqualTo(listOf("string1", "string2"))
        expectThat(input.listStringsField).isEqualTo(listOf("string1", "string2"))
        expectThat(input.objectField.stringField).isEqualTo("string")
        expectThrows<NoSuchElementException> { input.notAStringField }.message.isEqualTo("Value for field <notAStringField> is not a class kotlin.String but class kotlin.Int")
        expectThrows<NoSuchElementException> { input.noSuchField }.message.isEqualTo("Field <noSuchField> is missing")
        expectThrows<NoSuchElementException> { input.objectField.noSuchField }.message.isEqualTo("Field <noSuchField> is missing")
    }
}
