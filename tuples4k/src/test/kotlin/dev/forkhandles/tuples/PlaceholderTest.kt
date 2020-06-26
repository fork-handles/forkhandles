package dev.forkhandles.tuples

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

class PlaceholderTest {
    @Test
    fun placeholder() {
        assertThat(false, equalTo(false))
    }
}