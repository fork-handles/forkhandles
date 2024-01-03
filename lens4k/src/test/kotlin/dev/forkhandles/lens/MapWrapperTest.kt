package dev.forkhandles.lens

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import strikt.assertions.message

class MapWrapperTest {

    class MapBacked(propertySet: Map<String, Any?>) : MapWrapper(propertySet) {
        val stringField by LensProp<String>()
        val booleanField by LensProp<Boolean>()
        val intField by LensProp<Int>()
        val longField by LensProp<Long>()
        val decimalField by LensProp<Double>()
        val notAStringField by LensProp<String>()
        val noSuchField by LensProp<String>()
        val objectField by LensProp<String>()
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
                "objectField" to mapOf(
                    "stringField" to "string"
                )
            )
        )

        expectThat(mapBacked.stringField).isEqualTo("string")
        expectThat(mapBacked.booleanField).isEqualTo(true)
        expectThat(mapBacked.intField).isEqualTo(123)
        expectThat(mapBacked.longField).isEqualTo(Long.MAX_VALUE)
        expectThat(mapBacked.decimalField).isEqualTo(1.1234)
        expectThrows<NoSuchElementException> { mapBacked.notAStringField }.message.isEqualTo("Value for field <notAStringField> is not a class kotlin.String")
        expectThrows<NoSuchElementException> { mapBacked.noSuchField }.message.isEqualTo("Field <noSuchField> is missing")
    }
}
