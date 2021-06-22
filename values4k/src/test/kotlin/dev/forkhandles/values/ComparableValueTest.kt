package dev.forkhandles.values

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test


class ComparableValueTest {
    class MyComparableValue private constructor(value: String) :
        AbstractComparableValue<MyComparableValue,String>(value)
    {
        companion object : StringValueFactory<MyComparableValue>(::MyComparableValue)
    }
    
    @Test
    fun `comparable values`() {
        val myValue = MyComparableValue.of("hello")
        assertThat(myValue.compareTo(myValue), equalTo(0))
        assertThat(myValue.compareTo(MyComparableValue.of("hellz")), equalTo(-11))
        assertThat(myValue.compareTo(MyComparableValue.of("hella")), equalTo(14))
    }
}


