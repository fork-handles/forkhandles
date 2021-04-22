package dev.forkhandles.partial

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDate
import java.util.Locale

class PartialExampleTest {

    @Test
    fun footballers() {
        data class Footballer(val name: String, val dob: LocalDate, val locale: Locale)

        // you can create a partially applied function by a call to `partial`
        val english = ::Footballer.partial(`$2`, `$1`, Locale.UK)

        // or, if you import `dev.forkhandles.partial.invoke`, by currying
        val french = (::Footballer)(`$2`, `$1`, Locale.FRANCE)

        // or you can apply one argument at a time..
        val brazilian = (::Footballer)("Pelé")(LocalDate.of(1940, 20, 23))(Locale.forLanguageTag("pt_BR"))

        // The placeholders (`$1`, `$2`, etc.) specify the order in which parameters must be
        // passed to the partially applied function.  In this case, we have used them to
        // switch the order of the first parameters while binding the value of the last parameter.

        val davidBeckham = english(LocalDate.of(1975, 5, 2), "David Beckham")
        val ericCantona = french(LocalDate.of(1966, 5, 24), "Eric Cantona")

        assertEquals(
            Footballer("David Beckham", LocalDate.of(1975, 5, 2), Locale.UK),
            davidBeckham
        )
        assertEquals(
            Footballer("Eric Cantona", LocalDate.of(1966, 5, 24), Locale.FRANCE),
            ericCantona
        )
    }
}
