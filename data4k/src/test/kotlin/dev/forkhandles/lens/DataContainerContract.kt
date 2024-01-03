package dev.forkhandles.lens

import dev.forkhandles.values.IntValue
import dev.forkhandles.values.IntValueFactory
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.isNull
import strikt.assertions.message

interface MainClassFields {
    val stringField: String
    val booleanField: Boolean
    val intField: Int
    val longField: Long
    val decimalField: Double
    val notAStringField: String

    val mappedField: Int

    val listField: List<String>
    val listSubClassField: List<SubClassFields>
    val listIntsField: List<Int>
    val listValueField: List<MyType>

    val objectField: SubClassFields

    val valueField: MyType

    val optionalField: String?
    val optionalValueField: MyType?
    val optionalObjectField: SubClassFields?
    val optionalListField: List<String>?
}

interface SubClassFields {
    val stringField: String
    val noSuchField: String
}

class MyType private constructor(value: Int) : IntValue(value) {
    companion object : IntValueFactory<MyType>(::MyType)
}

abstract class DataContainerContract {

    abstract fun container(input: Map<String, Any?>): MainClassFields

    @Test
    fun `can get primitive values from properties`() {
        val input = container(
            mapOf(
                "stringField" to "string",
                "booleanField" to true,
                "intField" to 123,
                "longField" to Long.MAX_VALUE,
                "decimalField" to 1.1234,
                "notAStringField" to 123,
                "valueField" to 123,
                "mappedField" to "123",

                "optionalValueField" to 123,
                "optionalField" to "optional",
            )
        )

        expectThat(input.stringField).isEqualTo("string")
        expectThrows<NoSuchElementException> { container(mapOf()).stringField }.message.isEqualTo("Field <stringField> is missing")
        expectThrows<NoSuchElementException> { input.notAStringField }.message.isEqualTo("Value for field <notAStringField> is not a class kotlin.String but class kotlin.Int")

        expectThat(input.booleanField).isEqualTo(true)
        expectThat(input.intField).isEqualTo(123)
        expectThat(input.longField).isEqualTo(Long.MAX_VALUE)
        expectThat(input.decimalField).isEqualTo(1.1234)

        expectThat(input.mappedField).isEqualTo(123)
        expectThrows<ClassCastException> { container(mapOf("mappedField" to 123)).mappedField }
        expectThat(input.valueField).isEqualTo(MyType.of(123))

        expectThat(input.optionalField).isEqualTo("optional")
        expectThat(container(mapOf()).optionalField).isNull()

        expectThat(input.optionalValueField).isEqualTo(MyType.of(123))
        expectThat(container(mapOf()).optionalValueField).isNull()
    }

    @Test
    fun `object inputs`() {
        val input = container(
            mapOf(
                "objectField" to mapOf(
                    "stringField" to "string"
                ),
                "optionalObjectField" to mapOf(
                    "stringField" to "string"
                )
            )
        )

        expectThat(input.objectField.stringField).isEqualTo("string")
        expectThrows<NoSuchElementException> { input.objectField.noSuchField }.message.isEqualTo("Field <noSuchField> is missing")

        expectThat(input.optionalObjectField?.stringField).isEqualTo("string")
        expectThat(container(mapOf()).optionalObjectField).isNull()
    }

    @Test
    fun `list inputs`() {
        val listInput = container(
            mapOf(
                "listField" to listOf("string1", "string2"),
                "listIntsField" to listOf(1, 2, 3),
                "listValueField" to listOf(1, 2, 3),
                "listSubClassField" to listOf(
                    mapOf("stringField" to "string1"),
                    mapOf("stringField" to "string2"),
                ),
                "optionalListField" to listOf("hello")
            )
        )
        expectThat(listInput.listField).isEqualTo(listOf("string1", "string2"))
        expectThat(listInput.listIntsField).isEqualTo(listOf(1, 2, 3))
        expectThat(listInput.listValueField).isEqualTo(listOf(1, 2, 3).map(MyType::of))
        expectThat(listInput.listSubClassField.map { it.stringField }).isEqualTo(listOf("string1", "string2"))

        expectThat(listInput.optionalListField).isEqualTo(listOf("hello"))
        expectThat(container(mapOf()).optionalListField).isNull()
    }
}
