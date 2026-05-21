/*
 * Copyright (C) 2024 James Richardson
 *  - Originally Copyright (C) 2007 Amin Ahmad.
 * Licenced under GPL
 */

package dev.forkhandles.ropes4k.test

import dev.forkhandles.ropes4k.Rope
import dev.forkhandles.ropes4k.impl.ConcatenationRope
import dev.forkhandles.ropes4k.impl.FlatCharSequenceRope
import strikt.api.Assertion
import strikt.api.expectThat
import strikt.assertions.isEqualTo

@IgnorableReturnValue
fun <T : Rope> Assertion.Builder<T>.isString(s: String): Assertion.Builder<String> {
    return get { this.toString() }.isEqualTo(s)
}

internal fun concatenationRope(s: String): ConcatenationRope {
    when {
        s.isEmpty() -> {
            return ConcatenationRope(
                FlatCharSequenceRope(""),
                FlatCharSequenceRope(""),
            )
        }

        s.length == 1 -> {
            return ConcatenationRope(
                FlatCharSequenceRope(s),
                FlatCharSequenceRope(""),
            )
        }

        else -> {
            val mid = s.length / 2
            val concatenationRope = ConcatenationRope(
                FlatCharSequenceRope(s.substring(0, mid)),
                FlatCharSequenceRope(s.substring(mid, s.length))
            )
            expectThat(concatenationRope.length).isEqualTo(s.length)
            return concatenationRope
        }
    }
}
