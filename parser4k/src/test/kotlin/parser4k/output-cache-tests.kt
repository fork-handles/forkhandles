package parser4k

import org.junit.jupiter.api.Test
import parser4k.OutputCacheTests.Node.*
import parser4k.commonparsers.Tokens

class OutputCacheTests {
    private val logEvents = ArrayList<ParsingEvent>()
    private val log = ParsingLog { logEvents.add(it) }
    private val cache = OutputCache<Node>()

    private val integer = Tokens.integer.map { IntLiteral(it) }.with("int", log).with(cache)
    private val minus = inOrder(ref { expr }, str(" - "), ref { expr }).mapLeftAssoc(::Minus.asBinary()).with("minus", log).with(cache)
    private val plus = inOrder(ref { expr }, str(" + "), ref { expr }).mapLeftAssoc(::Plus.asBinary()).with("plus", log).with(cache)

    private val expr: Parser<Node> = oneOf(plus, minus, integer).reset(cache)

    @Test fun `use each parser once at each input offset`() {
        expectMinimalLog { "1".parseWith(expr) }
        expectMinimalLog { "1 + 2".parseWith(expr) }
        expectMinimalLog { "1 - 2".parseWith(expr) }
        expectMinimalLog { "1 + 2 + 3".parseWith(expr) }
        expectMinimalLog { "1 - 2 - 3".parseWith(expr) }
        expectMinimalLog { "1 - 2 + 3".parseWith(expr) }
    }

    @Test fun `log events after parsing a number`() {
        logEvents.clear()
        "123".parseWith(expr)

        logEvents.joinToString("\n") { it.toDebugString() } shouldEqual """
            "123" plus:0
            "123" plus:0 minus:0
            "123" plus:0 minus:0 int:0
            "123" plus:0 minus:0 int:0 -- 123
            "123" plus:0 minus:0 -- no match
            "123" plus:0 -- no match
        """.trimIndent()
    }

    private fun expectMinimalLog(f: () -> Unit) {
        logEvents.clear()
        f()
        val framesWithOutput = logEvents.filterIsInstance<AfterParsing<*>>().map { it.stackTrace.last() }
        framesWithOutput shouldEqual framesWithOutput.distinct()
    }

    private sealed class Node {
        data class IntLiteral(val value: String): Node()
        data class Plus(val left: Node, val right: Node): Node()
        data class Minus(val left: Node, val right: Node): Node()
    }
}