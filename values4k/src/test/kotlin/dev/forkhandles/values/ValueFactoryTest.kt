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
        assertThat(MyIntValue(123), equalTo(MyIntValue(123)))
        assertThat({ MyIntValue(0) }, throws<IllegalArgumentException>())
    }

    @Test
    fun `nullable factory`() {
        assertThat(MyValue.orNull("hello"), equalTo(MyValue("hello")))
        assertThat(MyValue.orNull(""), absent())
    }

    @Test
    fun `result factory`() {
        assertThat(MyValue.asResult4k("hello"), equalTo(Success(MyValue("hello"))))
        assertThat(MyValue.asResult4k("") is Failure<Exception>, equalTo(true))
    }

    @Test
    fun `throwable parse`() {
        assertThat(MyIntValue.parse("123"), equalTo(MyIntValue(123)))
        assertThat({ MyIntValue.parse("") }, throws<IllegalArgumentException>())
    }

    @Test
    fun `nullable parse`() {
        assertThat(MyIntValue.parseOrNull("123"), equalTo(MyIntValue(123)))
        assertThat(MyIntValue.parseOrNull(""), absent())
    }

    @Test
    fun `result parse`() {
        assertThat(MyIntValue.parseResult4k("123"), equalTo(Success(MyIntValue(123))))
        assertThat(MyValue.asResult4k("") is Failure<Exception>, equalTo(true))
    }
}
