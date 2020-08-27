package parser4k

import org.junit.jupiter.api.Test
import parser4k.OutputCacheTests.Node.*
import parser4k.commonparsers.Tokens

class OutputCacheTests {
    @Test fun `use each parser once at each input offset`() =
        object : TestGrammar() {
            override val expr = oneOf(
                plus.with(cache),
                minus.with(cache),
                integer.with(cache)
            ).reset(cache)
        }.run {
            expectMinimalLog { "1".parseWith(expr) }
            expectMinimalLog { "1 + 2".parseWith(expr) }
            expectMinimalLog { "1 - 2".parseWith(expr) }
            expectMinimalLog { "1 + 2 + 3".parseWith(expr) }
            expectMinimalLog { "1 - 2 - 3".parseWith(expr) }
            expectMinimalLog { "1 - 2 + 3".parseWith(expr) }
        }

    @Test fun `logged events after parsing a number`() =
        object : TestGrammar() {
            override val expr = oneOf(
                plus.with(cache),
                minus.with(cache),
                integer.with(cache)
            ).reset(cache)
        }.run {
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

    private abstract class TestGrammar {
        val logEvents = ArrayList<ParsingEvent>()
        private val log = ParsingLog { logEvents.add(it) }
        val cache = OutputCache<Node>()

        val integer = Tokens.integer.map { IntLiteral(it) }.with("int", log)
        val minus = inOrder(ref { expr }, str(" - "), ref { expr }).mapLeftAssoc(::Minus.asBinary()).with("minus", log)
        val plus = inOrder(ref { expr }, str(" + "), ref { expr }).mapLeftAssoc(::Plus.asBinary()).with("plus", log)

        abstract val expr: Parser<Node>

        fun expectMinimalLog(f: () -> Unit) {
            f()
            val framesWithOutput = logEvents.filterIsInstance<AfterParsing<*>>().map { it.stackTrace.last() }
            framesWithOutput shouldEqual framesWithOutput.distinct()
            logEvents.clear()
        }
    }

    private sealed class Node {
        data class IntLiteral(val value: String) : Node()
        data class Plus(val left: Node, val right: Node) : Node()
        data class Minus(val left: Node, val right: Node) : Node()
    }
}