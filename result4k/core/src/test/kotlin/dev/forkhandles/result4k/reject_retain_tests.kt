package dev.forkhandles.result4k

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class RejectRetainTests {
    sealed class Error
    data class TooSmall(val limit: Int) : Error()
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
    
    @Test
    fun `rejecting with error or null`() {
        val a = 20.asSuccess()
        
        fun filter(it: Int) = when {
            it < 10 -> TooSmall(limit = 10)
            it > 20 -> TooBig(limit = 20)
            else -> null
        }
        
        assertEquals(Failure(TooSmall(10)), 5.asSuccess().reject(::filter))
        assertEquals(Failure(TooBig(20)), 25.asSuccess().reject(::filter))
        assertEquals(Success(15), 15.asSuccess().reject(::filter))
    }
}
