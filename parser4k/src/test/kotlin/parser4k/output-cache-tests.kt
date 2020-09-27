@file:Suppress("ClassName")

package parser4k

import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import parser4k.PlusMinusTestGrammar.ASTNode.IntLiteral
import parser4k.PlusMinusTestGrammar.ASTNode.Minus
import parser4k.PlusMinusTestGrammar.ASTNode.Plus
import parser4k.commonparsers.Tokens

class `Parsing log example for cached plus-minus-int grammar` {
    private val logEvents = ArrayList<ParsingEvent>()
    private val log = ParsingLog { logEvents.add(it) }

    private val plusMinusGrammar = object : PlusMinusTestGrammar() {
        override val expr = oneOf(
            plus.with("plus", log).with(cache).with("plus-cache", log),
            minus.with("minus", log).with(cache).with("minus-cache", log),
            integer.with("int", log).with(cache).with("int-cache", log)
        ).reset(cache)
    }

    @Test
    fun `"123" parsing log`() {
        "123".parseWith(plusMinusGrammar.expr) shouldEqual IntLiteral(123)

        logEvents.toDebugString() shouldEqual """
            "123" plus-cache:0
            "123" plus-cache:0 plus:0
            "123" plus-cache:0 plus:0 plus-cache:0 -- no match
            "123" plus-cache:0 plus:0 minus-cache:0
            "123" plus-cache:0 plus:0 minus-cache:0 minus:0
            "123" plus-cache:0 plus:0 minus-cache:0 minus:0 plus-cache:0 -- no match
            "123" plus-cache:0 plus:0 minus-cache:0 minus:0 minus-cache:0 -- no match
            "123" plus-cache:0 plus:0 minus-cache:0 minus:0 int-cache:0
            "123" plus-cache:0 plus:0 minus-cache:0 minus:0 int-cache:0 int:0 -- 123
            "123" plus-cache:0 plus:0 minus-cache:0 minus:0 int-cache:0 -- 123
            "123" plus-cache:0 plus:0 minus-cache:0 minus:0 -- no match
            "123" plus-cache:0 plus:0 minus-cache:0 -- no match
            "123" plus-cache:0 plus:0 int-cache:0 -- 123
            "123" plus-cache:0 plus:0 -- no match
            "123" plus-cache:0 -- no match
            "123" minus-cache:0 -- no match
            "123" int-cache:0 -- 123
        """.trimIndent()
    }
}

class `Cached parsers are called at most one time at each offset` {
    private val logEvents = ArrayList<ParsingEvent>()
    private val log = ParsingLog { logEvents.add(it) }

    private val plusMinusGrammar = object : PlusMinusTestGrammar() {
        override val expr = oneOf(
            plus.with("plus", log).with(cache),
            minus.with("minus", log).with(cache),
            integer.with("int", log).with(cache)
        ).reset(cache)
    }

    @Test
    fun `"123" parsing log`() {
        "123".parseWith(plusMinusGrammar.expr)
        logEvents.toDebugString() shouldEqual """
            "123" plus:0
            "123" plus:0 minus:0
            "123" plus:0 minus:0 int:0 -- 123
            "123" plus:0 minus:0 -- no match
            "123" plus:0 -- no match
        """.trimIndent()
    }

    @Test
    fun `minimal parse log`() {
        parseWithMinimalLog("123")
        parseWithMinimalLog("1 + 2")
        parseWithMinimalLog("1 - 2")
        parseWithMinimalLog("1 + 2 + 3")
        parseWithMinimalLog("1 - 2 - 3")
        parseWithMinimalLog("1 - 2 + 3")
    }

    private fun parseWithMinimalLog(s: String) {
        val output = s.parseWith(plusMinusGrammar.expr)
        assertNotNull(output)
        expectMinimalParseLog(logEvents)
        logEvents.clear()
    }

    private fun expectMinimalParseLog(logEvents: List<ParsingEvent>) {
        val stackFrames = logEvents.filterIsInstance<AfterParsing<*>>().map { it.stackTrace.last() }
        stackFrames shouldEqual stackFrames.distinct()
    }
}

abstract class PlusMinusTestGrammar {
    val cache = OutputCache<ASTNode>()

    val integer = Tokens.integer.map { IntLiteral(it.toInt()) }
    val plus = inOrder(ref { expr }, str(" + "), ref { expr }).mapLeftAssoc(::Plus.asBinary())
    val minus = inOrder(ref { expr }, str(" - "), ref { expr }).mapLeftAssoc(::Minus.asBinary())

    abstract val expr: Parser<ASTNode>

    sealed class ASTNode {
        data class IntLiteral(val value: Int) : ASTNode()
        data class Plus(val left: ASTNode, val right: ASTNode) : ASTNode()
        data class Minus(val left: ASTNode, val right: ASTNode) : ASTNode()
    }
}

private fun List<ParsingEvent>.toDebugString(): String = collapseLeaves().joinToString("\n") { it.toDebugString() }

private fun List<ParsingEvent>.collapseLeaves(): List<ParsingEvent> {
    val result = ArrayList<ParsingEvent>()
    forEach { event ->
        when (event) {
            is BeforeParsing -> result.add(event)
            is AfterParsing<*> -> {
                val lastEvent = result.lastOrNull()
                if (lastEvent is BeforeParsing && lastEvent.stackTrace == event.stackTrace) {
                    result.removeAt(result.lastIndex)
                }
                result.add(event)
            }
        }
    }
    return result
}
