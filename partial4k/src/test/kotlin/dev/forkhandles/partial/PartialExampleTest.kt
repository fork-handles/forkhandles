package dev.forkhandles.partial

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.Locale
import dev.forkhandles.partial.curry.*


class PartialExampleTest {
    @Test
    fun footballers() {
        data class Footballer(val name: String, val dob: LocalDate, val locale: Locale)
        val english = ::Footballer.partial(`$1`, `$2`, Locale.UK)
        val french = ::Footballer.partial(`$1`, `$2`, Locale.FRANCE)
        
        val davidBeckham = english("David Beckham", LocalDate.of(1975, 5, 2))
        val ericCantona = french("Eric Cantona", LocalDate.of(1966, 5, 24))
        
        assertEquals(
            Footballer("David Beckham", LocalDate.of(1975, 5, 2), Locale.UK),
            davidBeckham
        )
        assertEquals(
            Footballer("Eric Cantona", LocalDate.of(1966, 5, 24), Locale.FRANCE),
            ericCantona
        )
    }
    
    @Test
    fun flipping() {
        fun f(x: String, y: Int, z: Double) = "$x,$y,$z"
        
        val f_zx = ::f.partial(`$2`, 10, `$1`)
    
        assertEquals("example,10,0.125", f_zx(0.125, "example"))
    }
    
    @Test
    fun currying() {
        fun f(x: String, y: Int, z: Double) = "$x,$y,$z"
    
        val f_zx = (::f)(`$2`, 10, `$1`)
    
        assertEquals("example,10,0.125", f_zx(0.125, "example"))
    }
}
