package dev.forkhandles.values

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test


class MyComparableValue private constructor(value: String) : StringValue(value),
    Comparable4k<String, MyComparableValue> {
    companion object : StringValueFactory<MyComparableValue>(::MyComparableValue)
}

class MixinsTest {
    @Test
    fun comparable() {
        val myValue = MyComparableValue.of("hello")
        assertThat(myValue.compareTo(myValue), equalTo(0))
        assertThat(myValue.compareTo(MyComparableValue.of("hellz")), equalTo(-11))
        assertThat(myValue.compareTo(MyComparableValue.of("hella")), equalTo(14))
    }
}
