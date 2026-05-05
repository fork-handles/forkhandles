package dev.forkhandles.result4k

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RejectRetainTests {
    sealed class Error
    data class TooBig(val limit: Int) : Error()
    data class TooBigWithActual(val limit: Int, val actual: Int) : Error()

    @Test
    fun `retaining values`() {
        val a = 10

        assertEquals(
            Success(a),
            a.asSuccess().retainIf({ it <= 15 }, otherwise = { TooBig(limit = 15) })
        )

        assertEquals(
            a.asSuccess().retainIf({ it <= 15 }, { TooBig(limit = 15) }),
            a.asSuccess().rejectIf({ it > 15 }, { TooBig(limit = 15) })
        )

        assertEquals(
            a.asSuccess().retainIf({ it <= 15 }, { TooBigWithActual(limit = 15, actual = it) }),
            a.asSuccess().rejectIf({ it > 15 }, { TooBigWithActual(limit = 15, actual = it) })
        )

        assertEquals(
            a.asSuccess().retainIfNotNull({ TooBig(limit = 15) }),
            a.asSuccess().rejectIfNull({ TooBig(limit = 15) })
        )
    }

    @Test
    fun `rejecting values`() {
        val a = 20

        assertEquals(
            Failure(TooBig(15)),
            a.asSuccess().retainIf({ it <= 15 }, otherwise = { TooBig(limit = 15) })
        )

        assertEquals(
            a.asSuccess().retainIf({ it <= 15 }, { TooBig(limit = 15) }),
            a.asSuccess().rejectIf({ it > 15 }, { TooBig(limit = 15) })
        )

        assertEquals(
            a.asSuccess().retainIf({ it <= 15 }, { TooBigWithActual(limit = 15, actual = it) }),
            a.asSuccess().rejectIf({ it > 15 }, { TooBigWithActual(limit = 15, actual = it) })
        )
    }
}
