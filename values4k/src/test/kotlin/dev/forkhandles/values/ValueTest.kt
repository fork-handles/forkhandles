package dev.forkhandles.values

import com.natpryce.hamkrest.MatchResult.Match
import com.natpryce.hamkrest.MatchResult.Mismatch
import com.natpryce.hamkrest.Matcher
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.throws
import dev.forkhandles.values.Maskers.hidden
import dev.forkhandles.values.Maskers.obfuscated
import dev.forkhandles.values.Maskers.substring
import org.junit.jupiter.api.Test

class MyValue(value: String) : Value<String>(value, String::isNotEmpty)
class HiddenValue(value: String) : Value<String>(value, masking = hidden('t'))
class ObsfucatedValue(value: String) : Value<String>(value, masking = obfuscated())
class SubstringValue(value: String) : Value<String>(value, masking = substring(3, 5))

class ValueTest {
    @Test
    fun `toString value`() {
        assertThat(MyValue("hellohello").toString(), equalTo("hellohello"))
        assertThat(HiddenValue("hellohello").toString(), equalTo("tttttttttt"))
        assertThat(SubstringValue("hellohello").toString(), equalTo("hel**ello"))
        assertThat(ObsfucatedValue("hello").toString(), object : Matcher<String> {
            override fun invoke(actual: String) =
                if (actual.all { it == '*' }) Match else Mismatch(actual)

            override val description: String = "all *"
        })
    }

    @Test
    fun `hashcode value`() {
        assertThat(MyValue("hello").hashCode(), equalTo("hello".hashCode()))
    }

    @Test
    fun equality() {
        val myValue = MyValue("hello")
        assertThat(myValue == myValue, equalTo(true))
        assertThat(myValue == MyValue("hello"), equalTo(true))
        assertThat(myValue == MyValue("hello2"), equalTo(false))
    }

    @Test
    fun `cannot create illegal value`() {
        assertThat({ MyValue("") }, throws<IllegalArgumentException>())
    }
}
