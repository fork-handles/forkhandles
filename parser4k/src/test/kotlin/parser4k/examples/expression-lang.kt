@file:Suppress("PackageDirectoryMismatch")

package parser4k.expressionlang

import org.junit.jupiter.api.Test
import parser4k.OutputCache
import parser4k.Parser
import parser4k.asBinary
import parser4k.commonparsers.Tokens
import parser4k.commonparsers.joinedWith
import parser4k.commonparsers.token
import parser4k.expressionlang.ExpressionLang.Expr.And
import parser4k.expressionlang.ExpressionLang.Expr.ArrayAccess
import parser4k.expressionlang.ExpressionLang.Expr.ArrayLiteral
import parser4k.expressionlang.ExpressionLang.Expr.Divide
import parser4k.expressionlang.ExpressionLang.Expr.Equal
import parser4k.expressionlang.ExpressionLang.Expr.False
import parser4k.expressionlang.ExpressionLang.Expr.FieldAccess
import parser4k.expressionlang.ExpressionLang.Expr.FunctionCall
import parser4k.expressionlang.ExpressionLang.Expr.Greater
import parser4k.expressionlang.ExpressionLang.Expr.IfThenElse
import parser4k.expressionlang.ExpressionLang.Expr.InArray
import parser4k.expressionlang.ExpressionLang.Expr.IntLiteral
import parser4k.expressionlang.ExpressionLang.Expr.Less
import parser4k.expressionlang.ExpressionLang.Expr.Minus
import parser4k.expressionlang.ExpressionLang.Expr.Multiply
import parser4k.expressionlang.ExpressionLang.Expr.Not
import parser4k.expressionlang.ExpressionLang.Expr.NotEqual
import parser4k.expressionlang.ExpressionLang.Expr.NotInArray
import parser4k.expressionlang.ExpressionLang.Expr.Or
import parser4k.expressionlang.ExpressionLang.Expr.Plus
import parser4k.expressionlang.ExpressionLang.Expr.StringLiteral
import parser4k.expressionlang.ExpressionLang.Expr.True
import parser4k.expressionlang.ExpressionLang.Expr.UnaryMinus
import parser4k.expressionlang.ExpressionLang.evaluate
import parser4k.expressionlang.ExpressionLang.parse
import parser4k.inOrder
import parser4k.map
import parser4k.mapLeftAssoc
import parser4k.nestedPrecedence
import parser4k.oneOf
import parser4k.oneOfLongest
import parser4k.oneOfWithPrecedence
import parser4k.parseWith
import parser4k.ref
import parser4k.reset
import parser4k.shouldEqual
import parser4k.skipWrapper
import parser4k.str
import parser4k.with
import java.util.Locale

private object ExpressionLang {
    private val cache = OutputCache<Expr>()

    private fun binaryExpr(tokenString: String, f: (Expr, Expr) -> Expr) =
        inOrder(ref { expr }, token(tokenString), ref { expr }).mapLeftAssoc(f.asBinary()).with(cache)

    private fun unaryExpr(tokenString: String, f: (Expr) -> Expr) =
        inOrder(token(tokenString), ref { expr }).map { (_, it) -> f(it) }.with(cache)

    private val boolLiteral = oneOf(str("true"), str("false")).map { if (it == "true") True else False }
    private val intLiteral = Tokens.integer.map { IntLiteral(it.toInt()) }
    private val stringLiteral = Tokens.string.map { StringLiteral(it) }
    private val arrayLiteral = inOrder(token("["), ref { expr }.joinedWith(token(",")), token("]"))
        .skipWrapper().map(::ArrayLiteral)
        .with(cache)

    private val equal = binaryExpr("==", ::Equal)
    private val notEqual = binaryExpr("!=", ::NotEqual)
    private val less = binaryExpr("<", ::Less)
    private val greater = binaryExpr(">", ::Greater)

    private val divide = binaryExpr("/", ::Divide)
    private val multiply = binaryExpr("*", ::Multiply)
    private val minus = binaryExpr("-", ::Minus)
    private val plus = binaryExpr("+", ::Plus)
    private val unaryMinus = unaryExpr("-", ::UnaryMinus)

    private val inArray = binaryExpr("in", ::InArray)
    private val notInArray = binaryExpr("not in", ::NotInArray)
    private val arrayAccess = inOrder(ref { expr }, token("["), ref { expr }, token("]"))
        .mapLeftAssoc { (left, _, right) -> ArrayAccess(left, right) }.with(cache)

    private val and = binaryExpr("and", ::And)
    private val or = binaryExpr("or", ::Or)
    private val not = unaryExpr("not", ::Not)
    private val ifThenElse = inOrder(token("if"), ref { expr }, token("then"), ref { expr }, token("else"), ref { expr })
        .map { (_, cond, _, ifTrue, _, ifFalse) -> IfThenElse(cond, ifTrue, ifFalse) }
        .with(cache)

    private val paren = inOrder(token("("), ref { expr }, token(")")).skipWrapper().with(cache)

    private val fieldAccess = inOrder(ref { expr }, token("."), Tokens.identifier)
        .mapLeftAssoc(::FieldAccess.asBinary()).with(cache)

    private val functionCall = inOrder(ref { expr }, token("."), Tokens.identifier, token("()"))
        .mapLeftAssoc { (left, _, name) -> FunctionCall(left, name) }.with(cache)

    private val expr: Parser<Expr> = oneOfWithPrecedence(
        ifThenElse,
        or,
        and,
        oneOf(inArray, notInArray),
        oneOf(equal, notEqual, less, greater),
        oneOf(plus, minus),
        oneOf(multiply, divide),
        oneOf(unaryMinus, not),
        oneOfLongest(
            fieldAccess,
            functionCall
        ),
        arrayAccess.nestedPrecedence(),
        paren.nestedPrecedence(),
        arrayLiteral.nestedPrecedence(),
        oneOf(stringLiteral, intLiteral, boolLiteral)
    ).reset(cache)

    fun parse(s: String): Expr = s.parseWith(expr)

    fun evaluate(s: String): Any = s.parseWith(expr).eval()

    sealed class Expr {
        object True : Expr()
        object False : Expr()
        data class IntLiteral(val value: Int) : Expr()
        data class StringLiteral(val value: String) : Expr()
        data class ArrayLiteral(val value: List<Expr>) : Expr()

        data class Equal(val left: Expr, val right: Expr) : Expr()
        data class NotEqual(val left: Expr, val right: Expr) : Expr()
        data class Less(val left: Expr, val right: Expr) : Expr()
        data class Greater(val left: Expr, val right: Expr) : Expr()

        data class Plus(val left: Expr, val right: Expr) : Expr()
        data class Minus(val left: Expr, val right: Expr) : Expr()
        data class Multiply(val left: Expr, val right: Expr) : Expr()
        data class Divide(val left: Expr, val right: Expr) : Expr()
        data class UnaryMinus(val value: Expr) : Expr()

        data class And(val left: Expr, val right: Expr) : Expr()
        data class Or(val left: Expr, val right: Expr) : Expr()
        data class Not(val value: Expr) : Expr()
        data class IfThenElse(val cond: Expr, val ifTrue: Expr, val ifFalse: Expr) : Expr()

        data class InArray(val elementExpr: Expr, val arrayExpr: Expr) : Expr()
        data class NotInArray(val elementExpr: Expr, val arrayExpr: Expr) : Expr()
        data class ArrayAccess(val arrayExpr: Expr, val indexExpr: Expr) : Expr()

        data class FieldAccess(val left: Expr, val name: String) : Expr()
        data class FunctionCall(val left: Expr, val name: String, val args: List<Expr> = emptyList()) : Expr()
    }

    private fun Expr.eval(): Any =
        when (this) {
            True -> true
            False -> false
            is IntLiteral -> value
            is StringLiteral -> value
            is ArrayLiteral -> value.map { it.eval() }

            is Equal -> left.eval() == right.eval()
            is NotEqual -> left.eval() != right.eval()
            is Less -> (left.eval() as Int) < (right.eval() as Int)
            is Greater -> (left.eval() as Int) > (right.eval() as Int)

            is Plus -> (left.eval() as Int) + (right.eval() as Int)
            is Minus -> (left.eval() as Int) - (right.eval() as Int)
            is Multiply -> (left.eval() as Int) * (right.eval() as Int)
            is Divide -> (left.eval() as Int) / (right.eval() as Int)
            is UnaryMinus -> -(value.eval() as Int)

            is InArray -> elementExpr.eval() in (arrayExpr.eval() as List<*>)
            is NotInArray -> elementExpr.eval() !in (arrayExpr.eval() as List<*>)
            is ArrayAccess -> (arrayExpr.eval() as List<*>)[indexExpr.eval() as Int]!!

            is And -> (left.eval() as Boolean) && (right.eval() as Boolean)
            is Or -> (left.eval() as Boolean) || (right.eval() as Boolean)
            is Not -> !(value.eval() as Boolean)
            is IfThenElse -> if (cond.eval() as Boolean) ifTrue.eval() else ifFalse.eval()

            // Not using reflection because it's as slow as all other tests.
            is FieldAccess -> {
                val obj = left.eval()
                when {
                    obj is List<*> && name == "size" -> obj.size
                    obj is String && name == "size" -> obj.length
                    else -> error("Unsupported field '$name' on $obj")
                }
            }

            // Not using reflection because it's as slow as all other tests.
            is FunctionCall -> {
                val obj = left.eval()
                when {
                    obj is List<*> && name == "reversed" -> obj.reversed()
                    obj is String && name == "toUpperCase" -> obj.uppercase()
                    obj is String && name == "uppercase" -> obj.uppercase()
                    else -> error("Unsupported field '$name' on $obj")
                }
            }
        }
}

class ExpressionLangTests {
    @Test
    fun literals() {
        evaluate("true") shouldEqual true

        evaluate("1") shouldEqual 1
        evaluate("123") shouldEqual 123

        evaluate("\"\"") shouldEqual ""
        evaluate("\"abc\"") shouldEqual "abc"
        evaluate("\"a\\\"c\"") shouldEqual """a\"c"""

        evaluate("[]") shouldEqual emptyList<Any>()
        evaluate("[1]") shouldEqual listOf(1)
        evaluate("[1 + 2, 3 * 4]") shouldEqual listOf(3, 12)
        evaluate("[1, 2, 3]") shouldEqual listOf(1, 2, 3)
        evaluate("[[1]]") shouldEqual listOf(listOf(1))
        evaluate("[[1, 2], [3]]") shouldEqual listOf(listOf(1, 2), listOf(3))
    }

    @Test
    fun comparison() {
        evaluate("true == true") shouldEqual true
        evaluate("true == false") shouldEqual false
        evaluate("\"foo\" == \"bar\"") shouldEqual false
        evaluate("[1, 2] == [1, 2]") shouldEqual true

        evaluate("1 != 2") shouldEqual true
        evaluate("123 != 123") shouldEqual false

        evaluate("1 < 2") shouldEqual true
        evaluate("2 < 2") shouldEqual false

        evaluate("2 > 1") shouldEqual true
        evaluate("2 > 2") shouldEqual false
    }

    @Test
    fun `array operations`() {
        evaluate("1 in []") shouldEqual false
        evaluate("1 in [\"\"]") shouldEqual false
        evaluate("1 in [1, 2, 3]") shouldEqual true

        evaluate("1 not in []") shouldEqual true
        evaluate("1 not in [\"\"]") shouldEqual true
        evaluate("1 not in [1, 2, 3]") shouldEqual false

        evaluate("[1, 2, 3][0]") shouldEqual 1
        evaluate("[1, 2, 3][1]") shouldEqual 2
        evaluate("[1, 2, 3][2]") shouldEqual 3
        evaluate("[1, 2, 3][3 - 1]") shouldEqual 3
        evaluate("[1, 2, 3][[1, 2, 3][1]]") shouldEqual 3

        evaluate("[[123]][0]") shouldEqual listOf(123)
        evaluate("[[123]][0][0]") shouldEqual 123
    }

    @Test
    fun `and, or, not expressions`() {
        evaluate("1==1 and 2==2") shouldEqual true
        evaluate("1==1 and 2==3") shouldEqual false
        evaluate("1==1 or 2==2") shouldEqual true
        evaluate("1==2 or 2==3") shouldEqual false
        evaluate("not false") shouldEqual true

        parse("1 and 2 or 3") shouldEqual Or(And(IntLiteral(1), IntLiteral(2)), IntLiteral(3))
        parse("1 or 2 and 3") shouldEqual Or(IntLiteral(1), And(IntLiteral(2), IntLiteral(3)))
        parse("1 or 2 or 3") shouldEqual Or(Or(IntLiteral(1), IntLiteral(2)), IntLiteral(3))
    }

    @Test
    fun `arithmetic expressions`() {
        evaluate("1 + 2") shouldEqual 3
        evaluate("1 - 2 + 3") shouldEqual 2
        evaluate("1 + 2 * 3") shouldEqual 7
        evaluate("1 + 2 * 3 / 4") shouldEqual 2

        evaluate("-1 + 2") shouldEqual 1
        evaluate("-1 + -2") shouldEqual -3
        evaluate("-1 - -2") shouldEqual 1
    }

    @Test
    fun `paren expressions`() {
        evaluate("(123)") shouldEqual 123
        evaluate("((123))") shouldEqual 123
        evaluate("((1 + 2) * 3 + 4)") shouldEqual 13

        parse("not (1 or 2)") shouldEqual Not(Or(IntLiteral(1), IntLiteral(2)))
    }

    @Test
    fun `if-then-else expressions`() {
        evaluate("if true then 1 else 2") shouldEqual 1
        evaluate("if false then 1 else 2") shouldEqual 2
        evaluate("if false then 1 else if false then 2 else 3") shouldEqual 3

        parse("if false then 1 else if false then 2 else 3") shouldEqual
            IfThenElse(False, IntLiteral(1), IfThenElse(False, IntLiteral(2), IntLiteral(3)))
    }

    @Test
    fun `field access expressions`() {
        evaluate("[0,1,2].size") shouldEqual 3
        evaluate("\"abcde\".size") shouldEqual 5

        parse("1.foo") shouldEqual FieldAccess(IntLiteral(1), "foo")
        parse("1.foo.bar") shouldEqual FieldAccess(FieldAccess(IntLiteral(1), "foo"), "bar")

        parse("1.foo - 2.bar") shouldEqual Minus(
            FieldAccess(IntLiteral(1), "foo"),
            FieldAccess(IntLiteral(2), "bar")
        )
    }

    @Test
    fun `function call expressions`() {
        evaluate("[0,1,2].reversed()") shouldEqual listOf(2, 1, 0)
        evaluate("[0,1,2].reversed().reversed()") shouldEqual listOf(0, 1, 2)
        evaluate("\"abcde\".toUpperCase()") shouldEqual "ABCDE"

        parse("1.foo()") shouldEqual FunctionCall(IntLiteral(1), "foo")
        parse("1.foo().bar()") shouldEqual FunctionCall(FunctionCall(IntLiteral(1), "foo"), "bar")
        parse("1.foo().bar") shouldEqual FieldAccess(FunctionCall(IntLiteral(1), "foo"), "bar")
        parse("1.foo.bar()") shouldEqual FunctionCall(FieldAccess(IntLiteral(1), "foo"), "bar")
        parse("1.foo.bar().baz") shouldEqual FieldAccess(FunctionCall(FieldAccess(IntLiteral(1), "foo"), "bar"), "baz")
    }
}
