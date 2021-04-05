package fabrikate4k.fabricators

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.present
import fabrikate4k.Fabrikate
import org.junit.jupiter.api.Test
import java.io.File
import java.math.BigDecimal
import java.math.BigInteger
import java.net.URI
import java.net.URL
import java.time.*
import java.util.*

class SupportedTypesTest {

    @Suppress("ArrayInDataClass")
    data class Foobar(
        val a: Int,
        val b: Long,
        val c: Double,
        val d: Float,
        val e: Char,
        val f: String,
        val g: ByteArray,
        val h: BigInteger,
        val i: BigDecimal,
        val j: Instant,
        val k: LocalDate,
        val l: LocalTime,
        val m: LocalDateTime,
        val n: OffsetTime,
        val o: OffsetDateTime,
        val p: ZonedDateTime,
        val q: Set<String>,
        val r: List<String>,
        val s: Map<String, String>,
        val t: URI,
        val u: URL,
        val v: Date,
        val w: File,
        val x: UUID,
        val y: Duration,
    )

    @Test
    fun `supports common types`() {
        assertThat(Fabrikate.randomInstance<Foobar>().also(::println), present())
    }
}
