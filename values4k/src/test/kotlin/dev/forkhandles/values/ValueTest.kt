package dev.forkhandles.values

import com.natpryce.hamkrest.MatchResult.Match
import com.natpryce.hamkrest.MatchResult.Mismatch
import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.values.Maskers.hidden
import dev.forkhandles.values.Maskers.obfuscated
import dev.forkhandles.values.Maskers.reveal
import dev.forkhandles.values.Maskers.substring
import org.junit.jupiter.api.Test

class MyValue private constructor(value: String) : StringValue(value) {
    companion object : StringValueFactory<MyValue>(::MyValue, String::isNotEmpty)
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

class SubstringValue private constructor(value: String) : StringValue(value, masking = substring(from = 3, to = 5)) {
    companion object : StringValueFactory<SubstringValue>(::SubstringValue)
}

class SubstringNoStartValue private constructor(value: String) : StringValue(value, masking = substring(to = 3)) {
    companion object : StringValueFactory<SubstringNoStartValue>(::SubstringNoStartValue)
}

class SubstringNoEndValue private constructor(value: String) : StringValue(value, masking = substring(from = 3)) {
    companion object : StringValueFactory<SubstringNoEndValue>(::SubstringNoEndValue)
}

class SubstringRevealValue private constructor(value: String) : StringValue(value, masking = reveal(from = 3, to = 5)) {
    companion object : StringValueFactory<SubstringRevealValue>(::SubstringRevealValue)
}

class SubstringRevealNoStartValue private constructor(value: String) : StringValue(value, masking = reveal(to = 3)) {
    companion object : StringValueFactory<SubstringRevealNoStartValue>(::SubstringRevealNoStartValue)
}

class SubstringRevealNoEndValue private constructor(value: String) : StringValue(value, masking = reveal(from = 3)) {
    companion object : StringValueFactory<SubstringRevealNoEndValue>(::SubstringRevealNoEndValue)
}

class ValueTest {
    @Test
    fun `toString value`() {
        assertThat(MyValue.of("1234567890").toString(), equalTo("1234567890"))
        assertThat(HiddenValue.of("1234567890").toString(), equalTo("tttttttttt"))

        assertThat(SubstringValue.of("1234567890").toString(), equalTo("123**7890"))
        assertThat(SubstringNoStartValue.of("1234567890").toString(), equalTo("***567890"))
        assertThat(SubstringNoEndValue.of("1234567890").toString(), equalTo("123******"))

        assertThat(SubstringRevealValue.of("1234567890").toString(), equalTo("***456****"))
        assertThat(SubstringRevealNoStartValue.of("1234567890").toString(), equalTo("1234******"))
        assertThat(SubstringRevealNoEndValue.of("1234567890").toString(), equalTo("***4567890"))

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
}
