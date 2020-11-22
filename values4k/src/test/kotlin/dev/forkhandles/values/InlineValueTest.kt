@file:Suppress("EXPERIMENTAL_FEATURE_WARNING")
package dev.forkhandles.values

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.throws
import org.junit.jupiter.api.Test

inline class MyInlineValue(val value: String) {
    companion object : InlineValue<String>(String::isNotEmpty)
}

class InlineValueTest {
    @Test
    fun `cannot create illegal value`() {
        assertThat({ MyInlineValue.of("") }, throws<IllegalArgumentException>())
    }
}
