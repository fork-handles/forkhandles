package parser4k

import dev.forkhandles.tuples.Tuple3
import org.junit.jupiter.api.Test

class LogTests {
    private val logEvents = ArrayList<ParsingEvent>()
    private val log = ParsingLog { logEvents.add(it) }

    private val boolean = oneOf(str("true"), str("false")).with("boolean", log)

    private val and = inOrder(
        ref { expr }.with("left", log),
        str(" && "),
        ref { expr }.with("right", log)
    ).with("and", log)

    private val or = inOrder(
        ref { expr }.with("left", log),
        str(" || "),
        ref { expr }.with("right", log)
    ).with("or", log)

    private val expr: Parser<Any> = oneOfWithPrecedence(or, and, boolean)

    @Test fun `boolean literal`() {
        "true || false".parseWith(expr) shouldEqual Tuple3("true", " || ", "false")

        logEvents.joinToString("\n") { it.toDebugString() } shouldEqual """
            "true || false" or:0
            "true || false" or:0 left:0
            "true || false" or:0 left:0 or:0
            "true || false" or:0 left:0 or:0 -- no match
            "true || false" or:0 left:0 and:0
            "true || false" or:0 left:0 and:0 left:0
            "true || false" or:0 left:0 and:0 left:0 and:0
            "true || false" or:0 left:0 and:0 left:0 and:0 -- no match
            "true || false" or:0 left:0 and:0 left:0 boolean:0
            "true || false" or:0 left:0 and:0 left:0 boolean:0 -- true
            "true || false" or:0 left:0 and:0 left:0 -- true
            "true || false" or:0 left:0 and:0 -- no match
            "true || false" or:0 left:0 boolean:0
            "true || false" or:0 left:0 boolean:0 -- true
            "true || false" or:0 left:0 -- true
            "true || false" or:0 right:8
            "true || false" or:0 right:8 or:8
            "true || false" or:0 right:8 or:8 left:8
            "true || false" or:0 right:8 or:8 left:8 or:8
            "true || false" or:0 right:8 or:8 left:8 or:8 -- no match
            "true || false" or:0 right:8 or:8 left:8 and:8
            "true || false" or:0 right:8 or:8 left:8 and:8 left:8
            "true || false" or:0 right:8 or:8 left:8 and:8 left:8 and:8
            "true || false" or:0 right:8 or:8 left:8 and:8 left:8 and:8 -- no match
            "true || false" or:0 right:8 or:8 left:8 and:8 left:8 boolean:8
            "true || false" or:0 right:8 or:8 left:8 and:8 left:8 boolean:8 -- false
            "true || false" or:0 right:8 or:8 left:8 and:8 left:8 -- false
            "true || false" or:0 right:8 or:8 left:8 and:8 -- no match
            "true || false" or:0 right:8 or:8 left:8 boolean:8
            "true || false" or:0 right:8 or:8 left:8 boolean:8 -- false
            "true || false" or:0 right:8 or:8 left:8 -- false
            "true || false" or:0 right:8 or:8 -- no match
            "true || false" or:0 right:8 and:8
            "true || false" or:0 right:8 and:8 left:8
            "true || false" or:0 right:8 and:8 left:8 and:8
            "true || false" or:0 right:8 and:8 left:8 and:8 -- no match
            "true || false" or:0 right:8 and:8 left:8 boolean:8
            "true || false" or:0 right:8 and:8 left:8 boolean:8 -- false
            "true || false" or:0 right:8 and:8 left:8 -- false
            "true || false" or:0 right:8 and:8 -- no match
            "true || false" or:0 right:8 boolean:8
            "true || false" or:0 right:8 boolean:8 -- false
            "true || false" or:0 right:8 -- false
            "true || false" or:0 -- true || false
        """.trimIndent()
    }
}