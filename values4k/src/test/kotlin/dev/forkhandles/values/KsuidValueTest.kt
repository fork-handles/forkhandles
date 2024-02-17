package dev.forkhandles.values

import com.github.ksuid.Ksuid
import com.github.ksuid.KsuidGenerator
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.lessThan
import org.junit.jupiter.api.Test
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset
import java.util.Random

class MyKsuidValue private constructor(value: Ksuid): KsuidValue(value) {
    companion object: KsuidValueFactory<MyKsuidValue>(::MyKsuidValue)
}

class KsuidValueTest {

    @Test
    fun `generate id`() {
        val time = Instant.parse("2023-09-18T12:00:00Z")
        val clock = Clock.fixed(time, ZoneOffset.UTC)
        val random = Random(1337)

        val ksuid = MyKsuidValue.random(clock, KsuidGenerator(random))
        assertThat(ksuid.value.toString(), equalTo("2VZKwSmOFrH28QKVG3qE3XVoMUp"))
        assertThat(ksuid.instant, equalTo(time))
    }

    @Test
    fun `parse id`() {
        val ksuid = MyKsuidValue.parse("2VaB6tloDoOktDfrzWsjLnnTe9T")
        assertThat(ksuid.instant, equalTo(Instant.parse("2023-09-18T19:08:58Z")))
        assertThat(ksuid.value.toString(), equalTo( "2VaB6tloDoOktDfrzWsjLnnTe9T"))
    }

    @Test
    fun `compare ids`() {
        val value1 = MyKsuidValue.parse("2VaFa3DfKMINqSUtdJoTtAMwsHU")
        val value2 = MyKsuidValue.parse("2VaFcctoWGZV2W7coyJab0p99Sa")
        assertThat(value1, lessThan(value2))
    }
}
