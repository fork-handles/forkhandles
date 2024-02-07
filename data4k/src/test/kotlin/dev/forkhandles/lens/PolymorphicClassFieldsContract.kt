package dev.forkhandles.lens

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.isNull
import strikt.assertions.message
import java.math.BigDecimal
import java.math.BigInteger
import kotlin.reflect.KMutableProperty0

interface PolymorphicClassFields : SimpleClassFields {
    var boolean: Boolean
    var int: Int
    var long: Long
    var double: Double
    var decimal: BigDecimal
    var bigInt: BigInteger
    var notAnInt: Int

    var listInts: List<Int>
    var listValue: List<MyType>

    var value: MyType

    val listMapped: List<String>
    var optionalMapped: Int?
    var optionalMappedList: List<Int>?

    val optionalValue: MyType?
    var optionalValueList: List<MyType>?
}

interface SubClassFields {
    var string: String
    var noSuch: String
}

interface PolymorphicClassFieldsContract : SimpleClassFieldsContract {

    override fun container(input: Map<String, Any?>): PolymorphicClassFields

    @Test
    fun `can read polymorphic values`() {
        val input = container(
            mapOf(
                "boolean" to true,
                "int" to 123,
                "long" to Long.MAX_VALUE,
                "double" to 1.1234,
                "decimal" to "1.1234",
                "notAnInt" to "123",
                "value" to 123,
                "mapped" to "123",
                "optionalValue" to 123,
            )
        )

        expectThrows<NoSuchElementException> { input.notAnInt }.message.isEqualTo("Value for field <notAnInt> is not a class kotlin.Int but class kotlin.String")

        expectThat(input.boolean).isEqualTo(true)
        expectThat(input.int).isEqualTo(123)
        expectThat(input.long).isEqualTo(Long.MAX_VALUE)
        expectThat(input.double).isEqualTo(1.1234)

        expectThrows<ClassCastException> { container(mapOf("mapped" to 123)).mapped }
        expectThat(input.value).isEqualTo(MyType.of(123))

        expectThat(input.optionalValue).isEqualTo(MyType.of(123))
        expectThat(container(mapOf()).optionalValue).isNull()
    }

    @Test
    fun `can write polymorphic values`() {
        val input = container(
            mapOf(
                "boolean" to true,
                "int" to 123,
                "long" to Long.MAX_VALUE,
                "double" to 1.1234,
                "value" to 123,
                "mapped" to "123",
                "optionalValue" to 123,
            )
        )

        expectSetWorks(input::standardField, "123")
        expectSetWorks(input::string, "123")
        expectSetWorks(input::boolean, false)
        expectSetWorks(input::int, 999)
        expectSetWorks(input::long, 0)
        expectSetWorks(input::double, 5.4536)

        expectSetWorks(input::optional, "123123")
        expectSetWorks(input::optional, null)
        expectSetWorks(input::mapped, 123)
    }
}

fun <T> expectSetWorks(prop: KMutableProperty0<T>, value: T) {
    prop.set(value)
    expectThat(prop.get()).isEqualTo(value)
}
