package dev.forkhandles.lens

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.isNull
import strikt.assertions.message

interface ComplexClassFields<T : SubClassFields> : PolymorphicClassFields {
    var listSubClass: List<T>
    var subClass: T
    var optionalSubClass: T?
    var optionalSubClassList: List<T>?
}

interface ComplexClassFieldsDataContainerContract<T : SubClassFields> : PolymorphicClassFieldsContract {

    override fun container(input: Map<String, Any?>): ComplexClassFields<T>

    fun subContainer(input: Map<String, Any?>): T

    @Test
    fun `read object values`() {
        val input = container(
            mapOf(
                "subClass" to mapOf(
                    "string" to "string"
                ),
                "optionalSubClass" to mapOf(
                    "string" to "string"
                )
            )
        )

        expectThat(input.subClass.string).isEqualTo("string")
        expectThrows<NoSuchElementException> { input.subClass.noSuch }.message.isEqualTo("Field <noSuch> is missing")

        expectThat(input.optionalSubClass?.string).isEqualTo("string")
        expectThat(container(mapOf()).optionalSubClass).isNull()
        expectThat(input.optionalSubClass?.string).isEqualTo("string")
    }

    @Test
    fun `write object values`() {
        val objFieldNext = mapOf(
            "string" to "string2"
        )
        val input = container(
            mapOf(
                "object" to objFieldNext,
                "optionalObject" to mapOf(
                    "string" to "string"
                )
            )
        )

        val nextObj = subContainer(objFieldNext)
        expectSetWorks(input::subClass, nextObj)
        expectThat(input.subClass).isEqualTo(subContainer(objFieldNext))

        expectSetWorks(input::optionalSubClass, nextObj)
        expectThat(input.optionalSubClass).isEqualTo(nextObj)
        expectSetWorks(input::optionalSubClass, null)
        expectThat(input.optionalSubClass).isEqualTo(null)
    }

    @Test
    fun `read list values`() {
        val input = container(
            mapOf(
                "list" to listOf("string1", "string2"),
                "listInts" to listOf(1, 2, 3),
                "listMapped" to listOf(123, 456),
                "listValue" to listOf(1, 2, 3),
                "listSubClass" to listOf(
                    mapOf("string" to "string1"),
                    mapOf("string" to "string2"),
                ),
                "optionalList" to listOf("hello")
            )
        )
        expectThat(input.list).isEqualTo(listOf("string1", "string2"))
        expectThat(input.listMapped).isEqualTo(listOf("123", "456"))
        expectThat(input.listInts).isEqualTo(listOf(1, 2, 3))
        expectThat(input.listValue).isEqualTo(listOf(1, 2, 3).map(MyType::of))
        expectThat(input.listSubClass.map { it.string }).isEqualTo(listOf("string1", "string2"))

        expectThat(input.optionalList).isEqualTo(listOf("hello"))
        expectThat(container(mapOf()).optionalList).isNull()
    }

    @Test
    fun `write list values`() {
        val input = container(
            mapOf(
                "list" to listOf("string1", "string2"),
                "listSubClass" to listOf(
                    mapOf("string" to "string1"),
                    mapOf("string" to "string2"),
                ),
                "listValue" to listOf(1, 2, 3),
                "optionalList" to listOf("hello")
            )
        )

        expectSetWorks(input::list, listOf("123"))
        expectSetWorks(input::listSubClass, listOf(subContainer(mapOf("123" to "123"))))
        expectSetWorks(input::listValue, listOf(MyType.of(123), MyType.of(456)))
        expectSetWorks(input::optionalSubClassList, listOf(subContainer(mapOf("123" to "123"))))
        expectSetWorks(input::optionalValueList, listOf(MyType.of(123), MyType.of(456)))
        expectSetWorks(input::optionalList, listOf("hello"))
    }
}
