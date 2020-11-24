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
        assertThat(MyIntValue.of(123), equalTo(MyIntValue.of(123)))
        assertThat({ MyIntValue.of(0) }, throws<IllegalArgumentException>())
    }

    @Test
    fun `nullable factory`() {
        assertThat(MyValue.ofNullable("hello"), equalTo(MyValue.of("hello")))
        assertThat(MyValue.ofNullable(""), absent())
    }

    @Test
    fun `result factory`() {
        assertThat(MyValue.ofResult4k("hello"), equalTo(Success(MyValue.of("hello"))))
        assertThat(MyValue.ofResult4k("") is Failure<Exception>, equalTo(true))
    }

    @Test
    fun `throwable parse`() {
        assertThat(MyIntValue.parse("123"), equalTo(MyIntValue.of(123)))
        assertThat({ MyIntValue.parse("") }, throws<IllegalArgumentException>())
    }

    @Test
    fun `nullable parse`() {
        assertThat(MyIntValue.parseNullable("123"), equalTo(MyIntValue.of(123)))
        assertThat(MyIntValue.parseNullable(""), absent())
    }

    @Test
    fun `result parse`() {
        assertThat(MyIntValue.parseResult4k("123"), equalTo(Success(MyIntValue.of(123))))
        assertThat(MyValue.ofResult4k("") is Failure<Exception>, equalTo(true))
    }
}
