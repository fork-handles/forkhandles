package dev.forkhandles.values

import kotlin.random.Random

/**
 * Strategy to mask data from prying eyes.
 */
typealias Masking<T> = T.() -> String

object Maskers {
    /**
     * Takes the toString() of the underlying value.
     */
    val public: Masking<Any> = { toString() }

    /**
     * Masks the content of the underlying value.
     */
    fun hidden(c: Char = '*'): Masking<Any> = { "$c".repeat(toString().length) }

    /**
     * Provides a random output which hides the length and content of the underlying value.
     */
    fun obfuscated(c: Char = '*'): Masking<Any> =
        { "$c".repeat(Random.nextInt(toString().length / 2, toString().length * 2)) }

    /**
     * Masks the specified substring of the value.
     */
    fun substring(from: Int = 0, to: Int? = null, c: Char = '*'): Masking<Any> = {
        toString().run {
            val range = IntRange(from, to ?: (length - 1))
            replaceRange(range, "$c".repeat(range.last - range.first))
        }
    }

    /**
     * Reveals the specified substring of the value.
     */
    fun reveal(from: Int = 0, to: Int? = null, c: Char = '*'): Masking<Any> = {
        toString().run {
            val startRange = (0 until from)
            val endRange = ((to ?: (length - 1)) + 1 until length)

            replaceRange(startRange, "$c".repeat(startRange.last - startRange.first + 1))
                .replaceRange(
                    endRange, "$c".repeat(endRange.last - endRange.first + 1)
                )
        }
    }
}
