package dev.forkhandles.values

import com.natpryce.hamkrest.MatchResult.Match
import com.natpryce.hamkrest.MatchResult.Mismatch
import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.values.Maskers.hidden
import dev.forkhandles.values.Maskers.obfuscated
import dev.forkhandles.values.Maskers.substring
import org.junit.jupiter.api.Test

class MyValue private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<MyValue>(::MyValue, String::isNotEmpty)
}

class MyComparableValue private constructor(value: String) : StringValue(value),
    ComparableValue<String, MyComparableValue> {
    companion object : StringValueFactory<MyComparableValue>(::MyComparableValue)
}

class MyIntValue private constructor(value: Int) : IntValue(value) {
    companion object : IntValueFactory<MyIntValue>(::MyIntValue, { it > 0 })
}

class HiddenValue private constructor(value: String) : StringValue(value, masking = hidden('t')) {
    companion object : StringValueFactory<HiddenValue>(::HiddenValue)
}

class ObsfucatedValue private constructor(value: String) : StringValue(value, masking = obfuscated()) {
    companion object : StringValueFactory<ObsfucatedValue>(::ObsfucatedValue)
}

class SubstringValue private constructor(value: String) : StringValue(value, masking = substring(3, 5)) {
    companion object : StringValueFactory<SubstringValue>(::SubstringValue)
}

class ValueTest {
    @Test
    fun `toString value`() {
        assertThat(MyValue.of("hellohello").toString(), equalTo("hellohello"))
        assertThat(HiddenValue.of("hellohello").toString(), equalTo("tttttttttt"))
        assertThat(SubstringValue.of("hellohello").toString(), equalTo("hel**ello"))
        assertThat(ObsfucatedValue.of("hello").toString(), object : Matcher<String> {
            override fun invoke(actual: String) =
                if (actual.all { it == '*' }) Match else Mismatch(actual)

            override val description: String = "all *"
        })
    }

    @Test
    fun `hashcode value`() {
        assertThat(MyValue.of("hello").hashCode(), equalTo("hello".hashCode()))
    }

    @Test
    fun equality() {
        val myValue = MyValue.of("hello")
        assertThat(myValue == myValue, equalTo(true))
        assertThat(myValue == MyValue.of("hello"), equalTo(true))
        assertThat(myValue == MyValue.of("hello2"), equalTo(false))
    }

    @Test
    fun comparable() {
        val myValue = MyComparableValue.of("hello")
        assertThat(myValue.compareTo(myValue), equalTo(0))
        assertThat(myValue.compareTo(MyComparableValue.of("hellz")), equalTo(-11))
        assertThat(myValue.compareTo(MyComparableValue.of("hella")), equalTo(14))
    }
}
