@file:Suppress("PackageDirectoryMismatch")

package parser4k.calculatortests

import org.junit.jupiter.api.Test
import parser4k.*
import parser4k.calculatortests.Calculator.Expression.*
import parser4k.calculatortests.Calculator.Expression.Number
import parser4k.commonparsers.token
import java.math.BigDecimal


private object Calculator {
    private val cache = OutputCache<Expression>()

    private val number = oneOrMore(oneOf('0'..'9')).map { Number(it.joinToString("").toBigDecimal()) }
    private val paren = inOrder(token("("), ref { expr }, token(")")).map { (_, it, _) -> it }
    private val divide = inOrder(ref { expr }, token("/"), ref { expr }).mapLeftAssoc(::Divide.asBinary()).with(cache)
    private val multiply = inOrder(ref { expr }, token("*"), ref { expr }).mapLeftAssoc(::Multiply.asBinary()).with(cache)
    private val minus = inOrder(ref { expr }, token("-"), ref { expr }).mapLeftAssoc(::Minus.asBinary()).with(cache)
    private val plus = inOrder(ref { expr }, token("+"), ref { expr }).mapLeftAssoc(::Plus.asBinary()).with(cache)
    private val power = inOrder(ref { expr }, token("^"), ref { expr }).map(::Power.asBinary()).with(cache)

    private val expr: Parser<Expression> = oneOfWithPrecedence(
        oneOf(plus, minus),
        oneOf(multiply, divide),
        power,
        paren.nestedPrecedence(),
        number
    ).reset(cache)

    fun evaluate(s: String) = s.parseWith(expr).evaluate()

    private fun Expression.evaluate(): BigDecimal =
        when (this) {
            is Number   -> value
            is Plus     -> left.evaluate() + right.evaluate()
            is Minus    -> left.evaluate() - right.evaluate()
            is Multiply -> left.evaluate() * right.evaluate()
            is Divide   -> left.evaluate().divide(right.evaluate())
            is Power    -> left.evaluate().pow(right.evaluate().toInt())
        }

    private sealed class Expression {
        data class Number(val value: BigDecimal) : Expression()
        data class Plus(val left: Expression, val right: Expression) : Expression()
        data class Minus(val left: Expression, val right: Expression) : Expression()
        data class Multiply(val left: Expression, val right: Expression) : Expression()
        data class Divide(val left: Expression, val right: Expression) : Expression()
        data class Power(val left: Expression, val right: Expression) : Expression()
    }
}

private object MinimalCalculator {
    val cache = OutputCache<BigDecimal>()
    fun binaryExpr(s: String) = inOrder(ref { expr }, token(s), ref { expr })

    val number = oneOrMore(oneOf('0'..'9')).map { it.joinToString("").toBigDecimal() }.with(cache)
    val paren = inOrder(token("("), ref { expr }, token(")")).skipWrapper().with(cache)

    val power = binaryExpr("^").map { (l, _, r) -> l.pow(r.toInt()) }.with(cache)
    val divide = binaryExpr("/").mapLeftAssoc { (l, _, r) -> l.divide(r) }.with(cache)
    val multiply = binaryExpr("*").mapLeftAssoc { (l, _, r) -> l * r }.with(cache)

    val minus = binaryExpr("-").mapLeftAssoc { (l, _, r) -> l - r }.with(cache)
    val plus = binaryExpr("+").mapLeftAssoc { (l, _, r) -> l + r }.with(cache)

    val expr: Parser<BigDecimal> = oneOfWithPrecedence(
        oneOf(plus, minus),
        oneOf(multiply, divide),
        power,
        paren.nestedPrecedence(),
        number
    ).reset(cache)

    fun evaluate(s: String) = s.parseWith(expr)
}


class CalculatorTests {
    @Test fun `valid input`() = listOf(Calculator::evaluate, MinimalCalculator::evaluate).forEach { evaluate ->
        evaluate("1") shouldEqual BigDecimal(1)
        evaluate("1 + 2") shouldEqual BigDecimal(3)
        evaluate("1 + 2 * 3") shouldEqual BigDecimal(7)
        evaluate("1 - 2 * 3") shouldEqual BigDecimal(-5)
        evaluate("1 - 2 * 3 + 4 / 5") shouldEqual BigDecimal("-4.2")
        evaluate("(1 + 2) * 3 - 4 / 5") shouldEqual BigDecimal("8.2")
        evaluate("(1 + 2) * (3 - 4) / 5") shouldEqual BigDecimal("-0.6")
        evaluate("2^12 - 2^10") shouldEqual BigDecimal(3072)
    }

    @Test fun `large valid input`() = listOf(Calculator::evaluate, MinimalCalculator::evaluate).forEach { evaluate ->
        evaluate(List(1000) { "1" }.joinToString("+")) shouldEqual BigDecimal(1000)
        evaluate(List(1000) { "1" }.joinToString("^")) shouldEqual BigDecimal(1)
    }

    @Test fun `invalid input`() {
        { Calculator.evaluate("+1") } shouldFailWith { it is NoMatchingParsers }
        { Calculator.evaluate("()") } shouldFailWith { it is NoMatchingParsers }

        { Calculator.evaluate("(1))") } shouldFailWithMessage """
            |
            |(1))
            |   ^
            |payload = Number(value=1)
        """

        { Calculator.evaluate("1 + 2 + ") } shouldFailWithMessage """
            |
            |1 + 2 + 
            |     ^
            |payload = Plus(left=Number(value=1), right=Number(value=2))
        """
    }
}