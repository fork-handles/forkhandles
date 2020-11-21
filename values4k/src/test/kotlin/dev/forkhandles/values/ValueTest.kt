package dev.forkhandles.values

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

class MyValue(value: String) : Value<String>(value)

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
}
