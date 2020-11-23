package dev.forkhandles.values

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.throws
import dev.forkhandles.result4k.Failure
import dev.forkhandles.result4k.Success
import org.junit.jupiter.api.Test

class ValueFactoryTest {

    @Test
    fun `throwable factory`() {
        assertThat(MyValue.of("hello"), equalTo(MyValue.of("hello")))
        assertThat({ MyValue.of("") }, throws<IllegalArgumentException>())
    }

    @Test
    fun `nullable factory`() {
        assertThat(MyValue.ofNullable("hello"), equalTo(MyValue.of("hello")))
        assertThat({ MyValue.ofNullable("") }, absent())
    }

    @Test
    fun `result factory`() {
        assertThat(MyValue.ofResult4k("hello"), equalTo(Success(MyValue.of("hello"))))
        assertThat(MyValue.ofResult4k("") is Failure<Exception>, equalTo(true))
    }
}
