@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")

package dev.forkhandles.values

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.throws
import org.junit.jupiter.api.Test

inline class MyInlineValue(val value: String) {
    companion object : InlineValue<MyInlineValue, String>(::MyInlineValue, String::isNotEmpty)
}

class InlineValueTest {
    @Test
    fun `create legal value`() {
        assertThat(MyInlineValue.of("hello"), equalTo(MyInlineValue("hello")))
    }

    @Test
    fun `cannot create illegal value`() {
        assertThat({ MyInlineValue.of("") }, throws<IllegalArgumentException>())
    }
}
