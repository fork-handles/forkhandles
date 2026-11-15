package dev.forkhandles.values

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.startsWith
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Clock
import java.time.Instant
import java.time.ZoneOffset.UTC
import java.util.UUID
import kotlin.random.Random

class TimeOrderedIdsTest {

    private fun clockAt(epochMillis: Long) = Clock.fixed(Instant.ofEpochMilli(epochMillis), UTC)

    @Test
    fun `v7 uuid encodes the clock time as a version 7 rfc uuid`() {
        val uuid = UUIDValueFactory(::TV).v7(clockAt(1645557742000), Random(0)).value

        assertThat(uuid.toString(), startsWith("017f22e2-79b0-7"))
        assertThat(uuid.version(), equalTo(7))
        assertThat(uuid.variant(), equalTo(2))
    }

    @Test
    fun `ulid round trips the spec example via its uuid and time`() {
        val ulid = ULID.parse("01ARZ3NDEKTSV4RRFFQ69G5FAV")

        assertThat(ulid.toUUID(), equalTo(UUID.fromString("01563e3a-b5d3-d676-4c61-efb99302bd5b")))
        assertThat(ulid.time, equalTo(Instant.ofEpochMilli(1469922850259)))
        assertThat(ulid.toString(), equalTo("01ARZ3NDEKTSV4RRFFQ69G5FAV"))
    }

    @Test
    fun `ulid can be built from a uuid or from its most and least significant bits`() {
        val expected = ULID.parse("01ARZ3NDEKTSV4RRFFQ69G5FAV")

        assertThat(ULID.fromUUID(UUID.fromString("01563e3a-b5d3-d676-4c61-efb99302bd5b")), equalTo(expected))
        assertThat(ULID(0x01563e3ab5d3d676L, 0x4c61efb99302bd5bL), equalTo(expected))
    }

    @Test
    fun `ulid parsing ignores case`() {
        assertThat(ULID.parse("01arz3ndektsv4rrffq69g5fav"), equalTo(ULID.parse("01ARZ3NDEKTSV4RRFFQ69G5FAV")))
    }

    @Test
    fun `ulid parsing rejects malformed values`() {
        listOf(
            "01ARZ3NDEKTSV4RRFFQ69G5FA",
            "01ARZ3NDEKTSV4RRFFQ69G5FAVV",
            "01ARZ3NDEKTSV4RRFFQ69G5FAU",
            "80000000000000000000000000"
        ).forEach { assertThrows<IllegalArgumentException>(it) { ULID.parse(it) } }
    }

    @Test
    fun `ulids sort in the same order as their string form`() {
        val values = listOf(
            "40000000000000000000000000",
            "3ZZZZZZZZZZZZZZZZZZZZZZZZZ",
            "00000000000008000000000000",
            "00000000000007ZZZZZZZZZZZZ",
            "01ARZ3NDEKTSV4RRFFQ69G5FAV"
        )

        assertThat(values.map(ULID::parse).sorted().map(ULID::toString), equalTo(values.sorted()))
    }

    @Test
    fun `ulid values parse from and show as the ulid string form`() {
        val factory = UlidValueFactory(::TV)

        val value = factory.parse("01arz3ndektsv4rrffq69g5fav")

        assertThat(value, equalTo(TV(ULID.fromUUID(UUID.fromString("01563e3a-b5d3-d676-4c61-efb99302bd5b")))))
        assertThat(factory.show(value), equalTo("01ARZ3NDEKTSV4RRFFQ69G5FAV"))
    }

    @Test
    fun `random ulid values start with the clock time`() {
        val value = UlidValueFactory(::TV).random(clockAt(1469922850259), Random(0))

        assertThat(value.value.toString(), startsWith("01ARZ3NDEK"))
    }
}
