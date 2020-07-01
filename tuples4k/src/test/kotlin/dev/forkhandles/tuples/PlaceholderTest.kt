package dev.forkhandles.tuples

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

class TupleExampleTest {
    @Test
    fun `just a little example`() {
        assertThat(tuple(1, 2, 3, 4, 5) + tuple(6, 7, 8), equalTo(tuple(1, 2, 3, 4, 5, 6, 7, 8)))
        assertThat(tuple(1,2,3,4,5).toList(), equalTo(listOf(1,2,3,4,5)))
    }
}
