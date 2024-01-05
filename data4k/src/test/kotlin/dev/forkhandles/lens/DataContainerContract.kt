package dev.forkhandles.lens

import dev.forkhandles.values.IntValue
import dev.forkhandles.values.IntValueFactory
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.isNull
import strikt.assertions.message
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KMutableProperty0

interface MainClassFields<T : SubClassFields> {
    var string: String
    var boolean: Boolean
    var int: Int
    var long: Long
    var double: Double
    var decimal: BigDecimal
    var bigInt: BigInteger
    var notAString: String

    var mapped: Int

    var list: List<String>
    var listSubClass: List<T>
    var listInts: List<Int>
    var listValue: List<MyType>
    val listMapped: List<String>

    var subClass: T

    var value: MyType

    var optional: String?
    var optionalMapped: Int?
    val optionalValue: MyType?
    var optionalSubClass: T?
    var optionalSubClassList: List<T>?
    var optionalList: List<String>?
    var optionalValueList: List<MyType>?
    var optionalMappedList: List<Int>?
}

interface SubClassFields {
    var string: String
    var noSuch: String
}

class MyType private constructor(value: Int) : IntValue(value) {
    companion object : IntValueFactory<MyType>(::MyType)
}

abstract class DataContainerContract<T : SubClassFields> {

    abstract fun container(input: Map<String, Any?>): MainClassFields<T>
    abstract fun subContainer(input: Map<String, Any?>): T

    @Test
    fun `can read primitives values`() {
        val input = container(
            mapOf(
                "string" to "string",
                "boolean" to true,
                "int" to 123,
                "long" to Long.MAX_VALUE,
                "double" to 1.1234,
                "decimal" to "1.1234",
                "notAString" to 123,
                "value" to 123,
                "mapped" to "123",

                "optionalValue" to 123,
                "optional" to "optional",
            )
        )

        expectThat(input.string).isEqualTo("string")
        expectThrows<NoSuchElementException> { container(mapOf()).string }.message.isEqualTo("Field <string> is missing")
        expectThrows<NoSuchElementException> { input.notAString }.message.isEqualTo("Value for field <notAString> is not a class kotlin.String but class kotlin.Int")

        expectThat(input.boolean).isEqualTo(true)
        expectThat(input.int).isEqualTo(123)
        expectThat(input.long).isEqualTo(Long.MAX_VALUE)
        expectThat(input.double).isEqualTo(1.1234)

        expectThat(input.mapped).isEqualTo(123)
        expectThrows<ClassCastException> { container(mapOf("mapped" to 123)).mapped }
        expectThat(input.value).isEqualTo(MyType.of(123))

        expectThat(input.optional).isEqualTo("optional")
        expectThat(container(mapOf()).optional).isNull()

        expectThat(input.optionalValue).isEqualTo(MyType.of(123))
        expectThat(container(mapOf()).optionalValue).isNull()
    }

    @Test
    fun `can write primitives values`() {
        val input = container(
            mapOf(
                "string" to "string",
                "boolean" to true,
                "int" to 123,
                "long" to Long.MAX_VALUE,
                "double" to 1.1234,
                "value" to 123,
                "mapped" to "123",

                "optionalValue" to 123,
                "optional" to "optional",
            )
        )

        expectSetWorks(input::string, "123")
        expectSetWorks(input::boolean, false)
        expectSetWorks(input::int, 999)
        expectSetWorks(input::long, 0)
        expectSetWorks(input::double, 5.4536)

        expectSetWorks(input::optional, "123123")
        expectSetWorks(input::optional, null)
        expectSetWorks(input::mapped, 123)
    }

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

    private fun <T> expectSetWorks(prop: KMutableProperty0<T>, value: T) {
        prop.set(value)
        expectThat(prop.get()).isEqualTo(value)
    }
}
