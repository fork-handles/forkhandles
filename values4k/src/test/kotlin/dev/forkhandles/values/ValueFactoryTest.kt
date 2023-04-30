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

        assertThat(MyIntValue.ofList(), equalTo(listOf()))
        assertThat(MyIntValue.ofList(123, 456), equalTo(listOf(MyIntValue.of(123), MyIntValue.of(456))))
        assertThat({ MyIntValue.ofList(0, 1) }, throws<IllegalArgumentException>())
    }

    @Test
    fun `throwable factory as function`() {
        assertThat(MyIntValue.of(123), equalTo(MyIntValue.of(123)))
        assertThat({ MyIntValue.of(0) }, throws<IllegalArgumentException>())
    }

    @Test
    fun `nullable factory`() {
        assertThat(MyValue.ofOrNull("hello"), equalTo(MyValue.of("hello")))
        assertThat(MyValue.ofOrNull(""), absent())

        assertThat(MyIntValue.ofListOrNull(), equalTo(emptyList()))
        assertThat(MyIntValue.ofListOrNull(123, 456), equalTo(listOf(MyIntValue.of(123), MyIntValue.of(456))))
        assertThat(MyValue.ofListOrNull(""), absent())
    }

    @Test
    fun `result4k factory`() {
        assertThat(MyValue.ofResult4k("hello"), equalTo(Success(MyValue.of("hello"))))
        assertThat(MyValue.ofResult4k("") is Failure<Exception>, equalTo(true))

        assertThat(MyValue.ofListResult4k(), equalTo(Success(listOf())))
        assertThat(
            MyValue.ofListResult4k("hello", "there"),
            equalTo(Success(listOf(MyValue.of("hello"), MyValue.of("there"))))
        )
        assertThat(MyValue.ofListResult4k("hello", "") is Failure<Exception>, equalTo(true))
    }

    @Test
    fun `kotlin result factory`() {
        assertThat(MyValue.ofResult("hello"), equalTo(Result.success(MyValue.of("hello"))))
        assertThat(MyValue.ofResult("").isFailure, equalTo(true))

        assertThat(MyValue.ofListResult(), equalTo(Result.success(listOf())))
        assertThat(
            MyValue.ofListResult("hello", "there"),
            equalTo(Result.success(listOf(MyValue.of("hello"), MyValue.of("there"))))
        )
        assertThat(MyValue.ofListResult("hello", "").isFailure, equalTo(true))
    }

    @Test
    fun `throwable parse`() {
        assertThat(MyIntValue.parse("123"), equalTo(MyIntValue.of(123)))
        assertThat({ MyIntValue.parse("") }, throws<IllegalArgumentException>())

        assertThat(MyIntValue.parseList(), equalTo(listOf()))
        assertThat(MyIntValue.parseList("123", "456"), equalTo(listOf(MyIntValue.of(123), MyIntValue.of(456))))
        assertThat({ MyIntValue.parseList("") }, throws<IllegalArgumentException>())
    }

    @Test
    fun `nullable parse`() {
        assertThat(MyIntValue.parseOrNull("123"), equalTo(MyIntValue.of(123)))
        assertThat(MyIntValue.parseOrNull(""), absent())

        assertThat(MyIntValue.parseListOrNull(), equalTo(listOf()))
        assertThat(MyIntValue.parseListOrNull("123", "456"), equalTo(listOf(MyIntValue.of(123), MyIntValue.of(456))))
        assertThat(MyIntValue.parseListOrNull(""), absent())
    }

    @Test
    fun `result parse`() {
        assertThat(MyIntValue.parseResult4k("123"), equalTo(Success(MyIntValue.of(123))))
        assertThat(MyValue.ofResult4k("") is Failure<Exception>, equalTo(true))

        assertThat(MyIntValue.parseListResult4k(), equalTo(Success(emptyList())))
        assertThat(
            MyIntValue.parseListResult4k("123", "456"),
            equalTo(Success(listOf(MyIntValue.of(123), MyIntValue.of(456))))
        )
        assertThat(MyValue.parseListResult4k("123", "") is Failure<Exception>, equalTo(true))
    }

    @Test
    fun show() {
        assertThat(MyIntValue.show(MyIntValue.of(123)), equalTo("123"))
        assertThat(MyBase64Value.show(MyBase64Value.of("ABC")), equalTo("ABC"))
        assertThat(MyBase36Value.show(MyBase36Value.of("ABC")), equalTo("ABC"))
        assertThat(MyBase32Value.show(MyBase32Value.of("ABC")), equalTo("ABC"))
        assertThat(Mybase16Value.show(Mybase16Value.of("ABC")), equalTo("ABC"))
    }

    @Test
    fun unwrap() {
        assertThat(MyIntValue.unwrap(MyIntValue.of(123)), equalTo(123))
    }

    @Test
    fun showList() {
        assertThat(MyIntValue.showList(MyIntValue.of(123), MyIntValue.of(456)), equalTo(listOf("123", "456")))
    }

}
