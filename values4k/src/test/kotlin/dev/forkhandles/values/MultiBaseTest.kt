package dev.forkhandles.values

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

class MyBase64Value private constructor(value: String) : StringValue(value) {
    companion object : Base64StringValueFactory<MyBase64Value>(::MyBase64Value)
}

class MyBase36Value private constructor(value: String) : StringValue(value) {
    companion object : Base36StringValueFactory<MyBase36Value>(::MyBase36Value)
}

class MyBase32Value private constructor(value: String) : StringValue(value) {
    companion object : Base32StringValueFactory<MyBase32Value>(::MyBase32Value)
}

class Mybase16Value private constructor(value: String) : StringValue(value) {
    companion object : Base16StringValueFactory<Mybase16Value>(::Mybase16Value)
}

class MultiBaseTest {

    @Test
    fun `parse base64 - valid`() {
        assertThat(MyBase64Value.parse("ABCDefgh123+/").value, equalTo("ABCDefgh123+/"))
    }

    @Test
    fun `parse base64 - with padding`() {
        assertThat(MyBase64Value.parse("Zm9vYmFyYmF6YmFuZw==").value, equalTo("Zm9vYmFyYmF6YmFuZw=="))
    }

    @Test
    fun `parse base64 - invalid`() {
        assertThat(MyBase64Value.parseOrNull("ABCD!!!"), absent())
    }

    @Test
    fun `encode base64`() {
        assertThat(MyBase64Value.encode("foobarbaz".encodeToByteArray()), equalTo(MyBase64Value.parse("Zm9vYmFyYmF6")))
    }

    @Test
    fun `parse base36 - valid`() {
        assertThat(MyBase36Value.parse("ABCD123").value, equalTo("ABCD123"))
    }

    @Test
    fun `parse base36 - strict casing`() {
        assertThat(MyBase36Value.parseOrNull("ABCDefgh123"), absent())
    }

    @Test
    fun `parse base36 - invalid`() {
        assertThat(MyBase36Value.parseOrNull("ABCD+/"), absent())
    }

    @Test
    fun `parse base32 - valid`() {
        assertThat(MyBase32Value.parse("ABCD23").value, equalTo("ABCD23"))
    }

    @Test
    fun `parse base32 - with padding`() {
        assertThat(MyBase32Value.parse("MZXW6YTBOJRGC6Q=").value, equalTo("MZXW6YTBOJRGC6Q="))
    }

    @Test
    fun `parse base32 - strict casing`() {
        assertThat(MyBase32Value.parseOrNull("ABcd23"), absent())
    }

    @Test
    fun `parse base32 - invalid`() {
        assertThat(MyBase32Value.parseOrNull("ABCD123"), absent())
    }

    @Test
    fun `parse base16 - valid`() {
        assertThat(Mybase16Value.parse("ABCD123").value, equalTo("ABCD123"))
    }

    @Test
    fun `parse base16 - strict casing`() {
        assertThat(Mybase16Value.parseOrNull("ABcd123"), absent())
    }

    @Test
    fun `parse base16 - no padding allowed`() {
        assertThat(Mybase16Value.parseOrNull("ABCD123="), absent())
    }

    @Test
    fun `parse base16 - invalid`() {
        assertThat(Mybase16Value.parseOrNull("ABYZ123"), absent())
    }

    @Test
    fun `encode base16`() {
        assertThat(Mybase16Value.encode("foobarbaz".encodeToByteArray()), equalTo(Mybase16Value.parse("666F6F62617262617A")))
    }
}
