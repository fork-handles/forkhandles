package dev.forkhandles.values

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.throws
import org.junit.jupiter.api.Test

class MyValue(value: String) : Value<String>(value, String::isNotEmpty)

class ValueTest {

    @Test
    fun `toString value`() {
        assertThat(MyValue("hello").toString(), equalTo("hello"))
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
        assertThat({ MyValue("")}, throws<IllegalArgumentException>())
    }
}
