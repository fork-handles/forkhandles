package dev.forkhandles.values

import java.math.BigInteger
import java.time.Instant
import java.util.UUID

@JvmInline
value class ULID private constructor(private val bits: UUID) : Comparable<ULID> {
    constructor(mostSignificantBits: Long, leastSignificantBits: Long) : this(UUID(mostSignificantBits, leastSignificantBits))

    val time: Instant get() = Instant.ofEpochMilli(bits.mostSignificantBits ushr 16)

    fun toUUID(): UUID = bits

    override fun compareTo(other: ULID) = compareValuesBy(
        this, other,
        { it.bits.mostSignificantBits.toULong() },
        { it.bits.leastSignificantBits.toULong() }
    )

    override fun toString() = toBigInteger().toString(32)
        .map { CROCKFORD[STANDARD.indexOf(it)] }
        .joinToString("")
        .padStart(26, '0')

    private fun toBigInteger() = BigInteger(1, ByteArray(16) { i ->
        val half = if (i < 8) bits.mostSignificantBits else bits.leastSignificantBits
        (half ushr (56 - 8 * (i % 8))).toByte()
    })

    companion object {
        private const val CROCKFORD = "0123456789ABCDEFGHJKMNPQRSTVWXYZ"
        private const val STANDARD = "0123456789abcdefghijklmnopqrstuv"
        private val FORMAT = "[0-7][$CROCKFORD]{25}".toRegex()

        fun fromUUID(uuid: UUID) = ULID(uuid)

        fun parse(value: String): ULID {
            val upper = value.uppercase()
            require(FORMAT.matches(upper)) { "Invalid ULID: $value" }
            val n = BigInteger(upper.map { STANDARD[CROCKFORD.indexOf(it)] }.joinToString(""), 32)
            return ULID(n.shiftRight(64).toLong(), n.toLong())
        }
    }
}
