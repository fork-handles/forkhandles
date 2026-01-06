package dev.forkhandles.values

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.closeTo
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.present
import com.ubertob.kondor.json.JAny
import com.ubertob.kondor.json.JsonStyle
import com.ubertob.kondor.json.jsonnode.JsonNodeObject
import com.ubertob.kondor.json.toJson
import com.ubertob.kondortools.expectSuccess
import com.ubertob.kondortools.isEquivalentJson
import org.junit.jupiter.api.Test
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant
import java.util.UUID

class KondorValuesTest {

    class IntId(value: Int) : IntValue(value) {
        companion object : IntValueFactory<IntId>(::IntId)
    }

    class LongId(value: Long) : LongValue(value) {
        companion object : LongValueFactory<LongId>(::LongId)
    }

    class DoubleId(value: Double) : DoubleValue(value) {
        companion object : DoubleValueFactory<DoubleId>(::DoubleId)
    }

    class FloatId(value: Float) : FloatValue(value) {
        companion object : FloatValueFactory<FloatId>(::FloatId)
    }

    class StringId(value: String) : StringValue(value) {
        companion object : StringValueFactory<StringId>(::StringId)
    }
    
    class BoolId(value: Boolean) : BooleanValue(value) {
        companion object : BooleanValueFactory<BoolId>(::BoolId)
    }
    
    data class PrimitiveValues(
        val anInt: IntId,
        val anIntNullable: IntId?,
        val aLong: LongId,
        val aLongNullable: LongId?,
        val aDouble: DoubleId,
        val aDoubleNullable: DoubleId?,
        val aFloat: FloatId,
        val aFloatNullable: FloatId?,
        val aString: StringId,
        val aStringNullable: StringId?,
        val aBool: BoolId,
        val aBoolNullable: BoolId?,
    )

    object JPrimitiveValues : JAny<PrimitiveValues>() {
        val an_int by num(IntId, PrimitiveValues::anInt)
        val an_int_n by num(IntId, PrimitiveValues::anIntNullable)
        val a_long by num(LongId, PrimitiveValues::aLong)
        val a_long_n by num(LongId, PrimitiveValues::aLongNullable)
        val a_double by num(DoubleId, PrimitiveValues::aDouble)
        val a_double_n by num(DoubleId, PrimitiveValues::aDoubleNullable)
        val a_float by num(FloatId, PrimitiveValues::aFloat)
        val a_float_n by num(FloatId, PrimitiveValues::aFloatNullable)
        val a_string by str(StringId, PrimitiveValues::aString)
        val a_string_n by str(StringId, PrimitiveValues::aStringNullable)
        val a_bool by bool(BoolId, PrimitiveValues::aBool)
        val a_bool_n by bool(BoolId, PrimitiveValues::aBoolNullable)

        override fun JsonNodeObject.deserializeOrThrow() =
            PrimitiveValues(
                +an_int, +an_int_n,
                +a_long, +a_long_n,
                +a_double, +a_double_n,
                +a_float, +a_float_n,
                +a_string, +a_string_n,
                +a_bool, + a_bool_n
            )
    }

    @Test
    fun `primitive values without nulls`() {
        val original = """{ 
            "an_int": 1, "an_int_n": 2,
            "a_long": 3, "a_long_n": 4,
            "a_double": 5.12, "a_double_n": 5.34,
            "a_float": 7.14, "a_float_n": 8.12,
            "a_string": "hello", "a_string_n": "world",
            "a_bool": true, "a_bool_n": false
            }
            """

        val p = JPrimitiveValues.fromJson(original).orThrow()
        assertThat(p.anInt, equalTo(IntId.of(1)))
        assertThat(p.anIntNullable, equalTo(IntId.of((2))))
        assertThat(p.aLong, equalTo(LongId.of(3)))
        assertThat(p.aLongNullable, equalTo(LongId.of(4)))
        assertThat(p.aDouble.value, closeTo(5.12,0.001))
        assertThat(p.aDoubleNullable?.value, present(closeTo(5.34, 0.001)))
        assertThat(p.aFloat.value, closeTo(7.14f, 0.001f))
        assertThat(p.aFloatNullable?.value, present(closeTo(8.12f, 0.001f)))

        JPrimitiveValues.toJson(p).isEquivalentJson(original).expectSuccess()

    }

    @Test
    fun `primitive values with nulls`() {
        val original = """{ 
            "an_int": 1, "an_int_n": null,
            "a_long": 3, "a_long_n": null,
            "a_double": 5.12, "a_double_n": null,
            "a_float": 7.14, "a_float_n": null,
            "a_string": "hello", "a_string_n": null,
            "a_bool": true, "a_bool_n": null
            }
            """
        val p = JPrimitiveValues.fromJson(original).orThrow()
        assertThat(p.anIntNullable, absent())
        assertThat(p.aLongNullable, absent())
        assertThat(p.aDoubleNullable, absent())
        assertThat(p.aFloatNullable, absent())
        assertThat(p.aStringNullable, absent())
        assertThat(p.aBoolNullable, absent())

        JPrimitiveValues.toJson(p, JsonStyle.prettyWithNulls).isEquivalentJson(original).expectSuccess()
    }


    class InstantId(value: Instant) : InstantValue(value) {
        companion object : InstantValueFactory<InstantId>(::InstantId)
    }
    
    class UUIDId(value: UUID) : UUIDValue(value) {
        companion object : UUIDValueFactory<UUIDId>(::UUIDId)
    }
    
    class BigDecimalId(value: BigDecimal) : BigDecimalValue(value) {
        companion object : BigDecimalValueFactory<BigDecimalId>(::BigDecimalId)
    }

    class BigIntegerId(value: BigInteger) : BigIntegerValue(value) {
        companion object : BigIntegerValueFactory<BigIntegerId>(::BigIntegerId)
    }

    data class OtherValues(
        val anInstant: InstantId,
        val anInstantNullable: InstantId?,
        val anotherInstant: InstantId,
        val anotherInstantNullable: InstantId?,
        val aUUID: UUIDId,
        val aUUIDNullable: UUIDId?,
        val aBigDecimal: BigDecimalId,
        val aBigDecimalNullable: BigDecimalId?,
        val aBigInteger: BigIntegerId,
        val aBigIntegerNullable: BigIntegerId?
    )

    object JOtherValues : JAny<OtherValues>() {
        val an_instant by str(InstantId, OtherValues::anInstant)
        val an_instant_n by str(InstantId, OtherValues::anInstantNullable)
        val another_instant by num(InstantId, OtherValues::anotherInstant)
        val another_instant_n by num(InstantId, OtherValues::anotherInstantNullable)
        val a_uuid by str(UUIDId, OtherValues::aUUID)
        val a_uuid_n by str(UUIDId, OtherValues::aUUIDNullable)
        val a_bd by num(BigDecimalId, OtherValues::aBigDecimal)
        val a_bd_n by num(BigDecimalId, OtherValues::aBigDecimalNullable)
        val a_bi by num(BigIntegerId, OtherValues::aBigInteger)
        val a_bi_n by num(BigIntegerId, OtherValues::aBigIntegerNullable)
        
        override fun JsonNodeObject.deserializeOrThrow() = OtherValues(
            +an_instant, +an_instant_n,
            +another_instant, +another_instant_n,
            +a_uuid, +a_uuid_n,
            +a_bd, +a_bd_n,
            +a_bi, +a_bi_n
        )
    }

    @Test
    fun `other values without nulls`() {
        val original = """{ 
            "an_instant": "2026-01-06T10:31:33.145867Z", "an_instant_n": "2027-01-06T10:31:33.145867Z",
            "another_instant": 1767695542470, "another_instant_n": 1767695542470,
            "a_uuid": "b6fa71eb-2bf0-4425-9688-0adb27d47f2b", "a_uuid_n": "b6fa71eb-2bf0-4425-9688-0adb27d47f2c",
            "a_bd": 7.14, "a_bd_n": 8.12,
            "a_bi": 1000, "a_bi_n": 2000,
            }
            """
        val p = JOtherValues.fromJson(original).orThrow()
        assertThat(p.anInstant, equalTo(InstantId.of(Instant.parse("2026-01-06T10:31:33.145867Z"))))
        assertThat(p.anInstantNullable, equalTo(InstantId.of(Instant.parse("2027-01-06T10:31:33.145867Z"))))
        assertThat(p.anotherInstant, equalTo(InstantId.of(Instant.parse("2026-01-06T10:32:22.470Z"))))
        assertThat(p.anotherInstantNullable, equalTo(InstantId.of(Instant.parse("2026-01-06T10:32:22.470Z"))))
        assertThat(p.aUUID, equalTo(UUIDId.of(UUID.fromString("b6fa71eb-2bf0-4425-9688-0adb27d47f2b"))))
        assertThat(p.aUUIDNullable, equalTo(UUIDId.of(UUID.fromString("b6fa71eb-2bf0-4425-9688-0adb27d47f2c"))))
        assertThat(p.aBigDecimal, equalTo(BigDecimalId.of(BigDecimal("7.14"))))
        assertThat(p.aBigDecimalNullable, equalTo(BigDecimalId.of(BigDecimal("8.12"))))
        assertThat(p.aBigInteger, equalTo(BigIntegerId.of(BigInteger("1000"))))
        assertThat(p.aBigIntegerNullable, equalTo(BigIntegerId.of(BigInteger("2000"))))

        JOtherValues.toJson(p).isEquivalentJson(original).expectSuccess()
    }

    @Test
    fun `other values with nulls`() {
        val original = """{ 
            "an_instant": "2026-01-06T10:31:33.145867Z", "an_instant_n": null,
            "another_instant": 1767695542470, "another_instant_n": null,
            "a_uuid": "b6fa71eb-2bf0-4425-9688-0adb27d47f2b", "a_uuid_n": null,
            "a_bd": 7.14, "a_bd_n": null,
            "a_bi": 1000, "a_bi_n": null,
            }
            """
        val p = JOtherValues.fromJson(original).orThrow()
        assertThat(p.anInstantNullable, absent())
        assertThat(p.anotherInstantNullable, absent())
        assertThat(p.aUUIDNullable, absent())
        assertThat(p.aBigDecimalNullable, absent())
        assertThat(p.aBigIntegerNullable, absent())

        JOtherValues.toJson(p, JsonStyle.prettyWithNulls).isEquivalentJson(original).expectSuccess()
    }
}
