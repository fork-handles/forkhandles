package dev.forkhandles.values

import com.ubertob.kondor.json.JBigDecimalRepresentable
import com.ubertob.kondor.json.JBigIntegerRepresentable
import com.ubertob.kondor.json.JBooleanRepresentable
import com.ubertob.kondor.json.JDoubleRepresentable
import com.ubertob.kondor.json.JField
import com.ubertob.kondor.json.JFieldMaybe
import com.ubertob.kondor.json.JFloatRepresentable
import com.ubertob.kondor.json.JIntRepresentable
import com.ubertob.kondor.json.JLongRepresentable
import com.ubertob.kondor.json.JStringRepresentable
import com.ubertob.kondor.json.datetime.JInstant
import com.ubertob.kondor.json.datetime.JInstantEpoch
import java.math.BigDecimal
import java.math.BigInteger
import java.time.Instant
import java.util.*


// The D in the class names refers to DOMAIN, the generic type of the ValueFactory

/**
 * Converters to allow use of Kondor JSON library seamlessly with values4k types:
 *
 * Given a field 'aString' in a class 'Bob', StringValue type 'MyStringValue', define the converter as:
 * val a_string by str(MyStringValue, Bob::aString)
 *
 * See the tests for many more examples
 */

class DJDoubleRepresentable<D : DoubleValue>(val vf: ValueFactory<D, Double>) : JDoubleRepresentable<D>() {
    override val cons: (Double) -> D = vf::of
    override val render: (D) -> Double = vf::unwrap
}

@JvmName("bindDoubleValue")
fun <PT : Any, D : DoubleValue> num(vf: ValueFactory<D, Double>, binder: PT.() -> D): JField<D, PT> {
    return JField(binder, DJDoubleRepresentable(vf))
}

@JvmName("bindDoubleValueNull")
fun <PT : Any, D : DoubleValue> num(vf: ValueFactory<D, Double>, binder: PT.() -> D?): JFieldMaybe<D, PT> {
    return JFieldMaybe(binder, DJDoubleRepresentable(vf))
}

class DJUUIDRepresentable<D : UUIDValue>(val vf: ValueFactory<D, UUID>) : JStringRepresentable<D>() {
    override val cons: (String) -> D = { vf.of(UUID.fromString(it)) }
    override val render: (D) -> String = { vf.unwrap(it).toString() }
}

@JvmName("bindUUIDValue")
fun <PT : Any, D : UUIDValue> str(vf: ValueFactory<D, UUID>, binder: PT.() -> D): JField<D, PT> {
    return JField(binder, DJUUIDRepresentable(vf))
}

@JvmName("bindUUIDValueNull")
fun <PT : Any, D : UUIDValue> str(vf: ValueFactory<D, UUID>, binder: PT.() -> D?): JFieldMaybe<D, PT> {
    return JFieldMaybe(binder, DJUUIDRepresentable(vf))
}

class DJInstantRepresentable<D : InstantValue>(val vf: ValueFactory<D, Instant>) : JStringRepresentable<D>() {
    override val cons: (String) -> D = { JInstant.cons(it).let(vf::of) }
    override val render: (D) -> String = { JInstant.render(vf.unwrap(it)) }
}

@JvmName("bindInstantValue")
fun <PT : Any, D : InstantValue> str(vf: ValueFactory<D, Instant>, binder: PT.() -> D): JField<D, PT> {
    return JField(binder, DJInstantRepresentable(vf))
}

@JvmName("bindInstantValueNull")
fun <PT : Any, D : InstantValue> str(vf: ValueFactory<D, Instant>, binder: PT.() -> D?): JFieldMaybe<D, PT> {
    return JFieldMaybe(binder, DJInstantRepresentable(vf))
}

class DJInstantEpochRepresentable<D : InstantValue>(val vf: ValueFactory<D, Instant>) : JLongRepresentable<D>() {
    override val cons: (Long) -> D = { JInstantEpoch.cons(it).let(vf::of) }
    override val render: (D) -> Long = { JInstantEpoch.render(vf.unwrap(it)) }
}

@JvmName("bindInstantEpochValue")
fun <PT : Any, D : InstantValue> num(vf: ValueFactory<D, Instant>, binder: PT.() -> D): JField<D, PT> {
    return JField(binder, DJInstantEpochRepresentable(vf))
}

@JvmName("bindInstantEpochValueNull")
fun <PT : Any, D : InstantValue> num(vf: ValueFactory<D, Instant>, binder: PT.() -> D?): JFieldMaybe<D, PT> {
    return JFieldMaybe(binder, DJInstantEpochRepresentable(vf))
}

class DJBigDecimalRepresentable<D : BigDecimalValue>(val vf: ValueFactory<D, BigDecimal>) :
    JBigDecimalRepresentable<D>() {
    override val cons: (BigDecimal) -> D = vf::of
    override val render: (D) -> BigDecimal = vf::unwrap
}

@JvmName("bindBigDecimalValue")
fun <PT : Any, D : BigDecimalValue> num(vf: ValueFactory<D, BigDecimal>, binder: PT.() -> D): JField<D, PT> {
    return JField(binder, DJBigDecimalRepresentable(vf))
}

@JvmName("bindBigDecimalValueNull")
fun <PT : Any, D : BigDecimalValue> num(vf: ValueFactory<D, BigDecimal>, binder: PT.() -> D?): JFieldMaybe<D, PT> {
    return JFieldMaybe(binder, DJBigDecimalRepresentable(vf))
}

class DJBigIntegerRepresentable<D : BigIntegerValue>(val vf: ValueFactory<D, BigInteger>) :
    JBigIntegerRepresentable<D>() {
    override val cons: (BigInteger) -> D = vf::of
    override val render: (D) -> BigInteger = vf::unwrap
}

@JvmName("bindBigIntegerValue")
fun <PT : Any, D : BigIntegerValue> num(vf: ValueFactory<D, BigInteger>, binder: PT.() -> D): JField<D, PT> {
    return JField(binder, DJBigIntegerRepresentable(vf))
}

@JvmName("bindBigIntegerValueNull")
fun <PT : Any, D : BigIntegerValue> num(vf: ValueFactory<D, BigInteger>, binder: PT.() -> D?): JFieldMaybe<D, PT> {
    return JFieldMaybe(binder, DJBigIntegerRepresentable(vf))
}

class DJBooleanRepresentable<D : BooleanValue>(val vf: ValueFactory<D, Boolean>) : JBooleanRepresentable<D>() {
    override val cons: (Boolean) -> D = vf::of
    override val render: (D) -> Boolean = vf::unwrap
}

@JvmName("bindBooleanValue")
fun <PT : Any, D : BooleanValue> bool(vf: ValueFactory<D, Boolean>, binder: PT.() -> D): JField<D, PT> {
    return JField(binder, DJBooleanRepresentable(vf))
}

@JvmName("bindBooleanValueNull")
fun <PT : Any, D : BooleanValue> bool(vf: ValueFactory<D, Boolean>, binder: PT.() -> D?): JFieldMaybe<D, PT> {
    return JFieldMaybe(binder, DJBooleanRepresentable(vf))
}

class DJFloatRepresentable<D : FloatValue>(val vf: ValueFactory<D, Float>) : JFloatRepresentable<D>() {
    override val cons: (Float) -> D = vf::of
    override val render: (D) -> Float = vf::unwrap
}

@JvmName("bindFloatValue")
fun <PT : Any, D : FloatValue> num(vf: ValueFactory<D, Float>, binder: PT.() -> D): JField<D, PT> {
    return JField(binder, DJFloatRepresentable(vf))
}

@JvmName("bindFloatValueNull")
fun <PT : Any, D : FloatValue> num(vf: ValueFactory<D, Float>, binder: PT.() -> D?): JFieldMaybe<D, PT> {
    return JFieldMaybe(binder, DJFloatRepresentable(vf))
}

class DJLongRepresentable<D : LongValue>(val vf: ValueFactory<D, Long>) : JLongRepresentable<D>() {
    override val cons: (Long) -> D = vf::of
    override val render: (D) -> Long = vf::unwrap
}

@JvmName("bindLongValue")
fun <PT : Any, D : LongValue> num(vf: ValueFactory<D, Long>, binder: PT.() -> D): JField<D, PT> {
    return JField(binder, DJLongRepresentable(vf))
}

@JvmName("bindLongValueNull")
fun <PT : Any, D : LongValue> num(vf: ValueFactory<D, Long>, binder: PT.() -> D?): JFieldMaybe<D, PT> {
    return JFieldMaybe(binder, DJLongRepresentable(vf))
}

class DJIntRepresentable<D : IntValue>(val vf: ValueFactory<D, Int>) : JIntRepresentable<D>() {
    override val cons: (Int) -> D = vf::of
    override val render: (D) -> Int = vf::unwrap
}

@JvmName("bindIntValue")
fun <PT : Any, D : IntValue> num(vf: ValueFactory<D, Int>, binder: PT.() -> D): JField<D, PT> {
    return JField(binder, DJIntRepresentable(vf))
}

@JvmName("bindIntValueNull")
fun <PT : Any, D : IntValue> num(vf: ValueFactory<D, Int>, binder: PT.() -> D?): JFieldMaybe<D, PT> {
    return JFieldMaybe(binder, DJIntRepresentable(vf))
}


class DJStringRepresentable<D : StringValue>(val vf: ValueFactory<D, String>) : JStringRepresentable<D>() {
    override val cons: (String) -> D = vf::of
    override val render: (D) -> String = vf::unwrap
}

@JvmName("bindStringValue")
fun <PT : Any, D : StringValue> str(vf: ValueFactory<D, String>, binder: PT.() -> D): JField<D, PT> {
    return JField(binder, DJStringRepresentable(vf))
}

@JvmName("bindStringValueNull")
fun <PT : Any, D : StringValue> str(vf: ValueFactory<D, String>, binder: PT.() -> D?): JFieldMaybe<D, PT> {
    return JFieldMaybe(binder, DJStringRepresentable(vf))
}
