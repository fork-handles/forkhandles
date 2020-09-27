package parser4k

import org.junit.jupiter.api.Test

class LeftAssociativityTests {
    @Test
    fun `single unary operator`() =
        object : TestGrammar() {
            val foo = inOrder(ref { expr }, str(".foo")).mapLeftAssoc { (expr, _) -> Field(expr, "foo") }
            override val expr: Parser<ASTNode> = oneOf(
                foo,
                number
            )
        }.run {
            "1" shouldBeParsedAs "1"
            "1.foo" shouldBeParsedAs "(1.foo)"
            "1.foo.foo" shouldBeParsedAs "((1.foo).foo)"
        }

    @Test
    fun `two unary operators`() =
        object : TestGrammar() {
            val foo = inOrder(ref { expr }, str(".foo")).mapLeftAssoc { (expr, _) -> Field(expr, "foo") }
            val bar = inOrder(ref { expr }, str(".bar")).mapLeftAssoc { (expr, _) -> Field(expr, "bar") }
            override val expr: Parser<ASTNode> = oneOf(
                foo,
                bar,
                number
            )
        }.run {
            "1" shouldBeParsedAs "1"

            "1.foo" shouldBeParsedAs "(1.foo)"
            "1.foo.foo" shouldBeParsedAs "((1.foo).foo)"

            "1.bar" shouldBeParsedAs "(1.bar)"
            "1.bar.bar" shouldBeParsedAs "((1.bar).bar)"

            "1.foo.bar" shouldBeParsedAs "((1.foo).bar)"
            "1.bar.foo" shouldBeParsedAs "((1.bar).foo)"
        }

    @Test
    fun `single binary operator`() =
        object : TestGrammar() {
            val plus = inOrder(ref { expr }, str(" + "), ref { expr }).mapLeftAssoc(::Plus.asBinary())
            override val expr: Parser<ASTNode> = oneOf(
                plus,
                number
            )
        }.run {
            "1" shouldBeParsedAs "1"
            "1 + 2" shouldBeParsedAs "(1 + 2)"
            "1 + 2 + 3" shouldBeParsedAs "((1 + 2) + 3)"
        }

    @Test
    fun `two binary operators`() =
        object : TestGrammar() {
            val plus = inOrder(ref { expr }, str(" + "), ref { expr }).mapLeftAssoc(::Plus.asBinary())
            val minus = inOrder(ref { expr }, str(" - "), ref { expr }).mapLeftAssoc(::Minus.asBinary())
            override val expr: Parser<ASTNode> = oneOf(
                plus,
                minus,
                number
            )
        }.run {
            "1" shouldBeParsedAs "1"

            "1 + 2" shouldBeParsedAs "(1 + 2)"
            "1 + 2 + 3" shouldBeParsedAs "((1 + 2) + 3)"

            "1 - 2" shouldBeParsedAs "(1 - 2)"
            "1 - 2 - 3" shouldBeParsedAs "((1 - 2) - 3)"

            "1 + 2 - 3" shouldBeParsedAs "((1 + 2) - 3)"
            "1 - 2 + 3" shouldBeParsedAs "((1 - 2) + 3)"
            "1 + 2 - 3 + 4" shouldBeParsedAs "(((1 + 2) - 3) + 4)"
        }

    @Test
    fun `operator precedence`() =
        object : TestGrammar() {
            val plus = inOrder(ref { expr }, str(" + "), ref { expr }).mapLeftAssoc(::Plus.asBinary())
            val or = inOrder(ref { expr }, str(" || "), ref { expr }).mapLeftAssoc(::Or.asBinary())
            override val expr: Parser<ASTNode> = oneOfWithPrecedence(
                or,
                plus,
                number
            )
        }.run {
            "1" shouldBeParsedAs "1"

            "1 + 2" shouldBeParsedAs "(1 + 2)"
            "1 + 2 + 3" shouldBeParsedAs "((1 + 2) + 3)"

            "1 || 2" shouldBeParsedAs "(1 || 2)"
            "1 || 2 || 3" shouldBeParsedAs "((1 || 2) || 3)"

            "1 + 2 || 3" shouldBeParsedAs "((1 + 2) || 3)"
            "1 || 2 + 3" shouldBeParsedAs "(1 || (2 + 3))"
            "1 + 2 || 3 + 4" shouldBeParsedAs "((1 + 2) || (3 + 4))"
            "1 || 2 + 3 || 4" shouldBeParsedAs "((1 || (2 + 3)) || 4)"
        }

    @Test
    fun `nested operator precedence`() =
        object : TestGrammar() {
            val plus = inOrder(ref { expr }, str(" + "), ref { expr }).mapLeftAssoc(::Plus.asBinary())
            val paren = inOrder(str("("), ref { expr }, str(")")).map { (_, it, _) -> it }
            override val expr: Parser<ASTNode> = oneOfWithPrecedence(
                plus,
                paren.nestedPrecedence(),
                number
            )
        }.run {
            "1" shouldBeParsedAs "1"

            "1 + 2" shouldBeParsedAs "(1 + 2)"
            "1 + 2 + 3" shouldBeParsedAs "((1 + 2) + 3)"

            "(1)" shouldBeParsedAs "1"
            "((1))" shouldBeParsedAs "1"
            "(1) + (2)" shouldBeParsedAs "(1 + 2)"

            "(1 + 2) + 3" shouldBeParsedAs "((1 + 2) + 3)"
            "1 + (2 + 3)" shouldBeParsedAs "(1 + (2 + 3))"

            "(1 + 2) + 3 + 4" shouldBeParsedAs "(((1 + 2) + 3) + 4)"
            "1 + (2 + 3) + 4" shouldBeParsedAs "((1 + (2 + 3)) + 4)"
            "1 + 2 + (3 + 4)" shouldBeParsedAs "((1 + 2) + (3 + 4))"
        }

    @Test
    fun `nested left-associative operator precedence`() =
        object : TestGrammar() {
            val plus = inOrder(ref { expr }, str(" + "), ref { expr })
                .mapLeftAssoc(::Plus.asBinary())

            val accessByIndex = inOrder(ref { expr }, str("["), ref { expr }, str("]"))
                .mapLeftAssoc { (left, _, right, _) -> AccessByIndex(left, right) }

            override val expr: Parser<ASTNode> = oneOfWithPrecedence(
                plus,
                accessByIndex.nestedPrecedence(),
                number
            )
        }.run {
            "123" shouldBeParsedAs "123"
            "1 + 2" shouldBeParsedAs "(1 + 2)"
            "1 + 2 + 3" shouldBeParsedAs "((1 + 2) + 3)"

            "123[0]" shouldBeParsedAs "123[0]"
            "123[0][1]" shouldBeParsedAs "123[0][1]"
            "123[1 + 2]" shouldBeParsedAs "123[(1 + 2)]"
            "123[1 + 2] + 3" shouldBeParsedAs "(123[(1 + 2)] + 3)"
        }

    @Test
    fun `nested left-associative operator precedence with cache`() =
        object : TestGrammar() {
            val plus = inOrder(ref { expr }, str(" + "), ref { expr })
                .mapLeftAssoc(::Plus.asBinary())

            val accessByIndex = inOrder(ref { expr }, str("["), ref { expr }, str("]"))
                .mapLeftAssoc { (left, _, right, _) -> AccessByIndex(left, right) }

            override val expr: Parser<ASTNode> = oneOfWithPrecedence(
                plus.with(cache),
                accessByIndex.with(cache).nestedPrecedence(),
                number.with(cache)
            ).reset(cache)
        }.run {
            "123" shouldBeParsedAs "123"
            "1 + 2" shouldBeParsedAs "(1 + 2)"
            "1 + 2 + 3" shouldBeParsedAs "((1 + 2) + 3)"

            "123[0]" shouldBeParsedAs "123[0]"
            "123[0][1]" shouldBeParsedAs "123[0][1]"
            "123[1 + 2]" shouldBeParsedAs "123[(1 + 2)]"
            "123[1 + 2] + 3" shouldBeParsedAs "(123[(1 + 2)] + 3)"
        }
}

class RightAssociativityTests {
    @Test
    fun `single unary operator`() =
        object : TestGrammar() {
            val preIncrement = inOrder(str("++"), ref { expr }).map { (_, it) -> PreIncrement(it) }
            override val expr: Parser<ASTNode> = oneOf(
                preIncrement,
                number
            )
        }.run {
            "123" shouldBeParsedAs "123"
            "++123" shouldBeParsedAs "++(123)"
            "++++123" shouldBeParsedAs "++(++(123))"
        }

    @Test
    fun `two unary operators`() =
        object : TestGrammar() {
            val preIncrement = inOrder(str("++"), ref { expr }).map { (_, it) -> PreIncrement(it) }
            val preDecrement = inOrder(str("--"), ref { expr }).map { (_, it) -> PreDecrement(it) }
            override val expr: Parser<ASTNode> = oneOf(
                preIncrement,
                preDecrement,
                number
            )
        }.run {
            "123" shouldBeParsedAs "123"

            "++123" shouldBeParsedAs "++(123)"
            "++++123" shouldBeParsedAs "++(++(123))"

            "--123" shouldBeParsedAs "--(123)"
            "----123" shouldBeParsedAs "--(--(123))"

            "++--123" shouldBeParsedAs "++(--(123))"
            "--++123" shouldBeParsedAs "--(++(123))"
        }

    @Test
    fun `single binary operator`() =
        object : TestGrammar() {
            val power = inOrder(ref { expr }, str(" ^ "), ref { expr }).map(::Power.asBinary())
            override val expr: Parser<ASTNode> = oneOf(
                power,
                number
            )
        }.run {
            "1" shouldBeParsedAs "1"
            "1 ^ 2" shouldBeParsedAs "(1 ^ 2)"
            "1 ^ 2 ^ 3" shouldBeParsedAs "(1 ^ (2 ^ 3))"
        }

    @Test
    fun `two binary operators`() =
        object : TestGrammar() {
            val power = inOrder(ref { expr }, str(" ^ "), ref { expr }).map(::Power.asBinary())
            val colon = inOrder(ref { expr }, str(" : "), ref { expr }).map(::Colon.asBinary())
            override val expr: Parser<ASTNode> = oneOf(
                power,
                colon,
                number
            )
        }.run {
            "1" shouldBeParsedAs "1"

            "1 ^ 2" shouldBeParsedAs "(1 ^ 2)"
            "1 ^ 2 ^ 3" shouldBeParsedAs "(1 ^ (2 ^ 3))"

            "1 : 2" shouldBeParsedAs "(1 : 2)"
            "1 : 2 : 3" shouldBeParsedAs "(1 : (2 : 3))"

            "1 ^ 2 : 3" shouldBeParsedAs "(1 ^ (2 : 3))"
            "1 : 2 ^ 3" shouldBeParsedAs "(1 : (2 ^ 3))"
            "1 ^ 2 : 3 ^ 4" shouldBeParsedAs "(1 ^ (2 : (3 ^ 4)))"
        }

    @Test
    fun `operator precedence`() =
        object : TestGrammar() {
            val power = inOrder(ref { expr }, str(" ^ "), ref { expr }).map(::Power.asBinary())
            val and = inOrder(ref { expr }, str(" && "), ref { expr }).map(::And.asBinary())
            override val expr: Parser<ASTNode> = oneOfWithPrecedence(
                and, // this is on purpose a right-associative AND (even though it's normally left-associative)
                power,
                number
            )
        }.run {
            "1" shouldBeParsedAs "1"

            "1 ^ 2" shouldBeParsedAs "(1 ^ 2)"
            "1 ^ 2 ^ 3" shouldBeParsedAs "(1 ^ (2 ^ 3))"

            "1 && 2" shouldBeParsedAs "(1 && 2)"
            "1 && 2 && 3" shouldBeParsedAs "(1 && (2 && 3))"

            "1 ^ 2 && 3" shouldBeParsedAs "((1 ^ 2) && 3)"
            "1 && 2 ^ 3" shouldBeParsedAs "(1 && (2 ^ 3))"
            "1 ^ 2 && 3 ^ 4" shouldBeParsedAs "((1 ^ 2) && (3 ^ 4))"
            "1 && 2 ^ 3 && 4" shouldBeParsedAs "(1 && ((2 ^ 3) && 4))"
        }

    @Test
    fun `nested operator precedence`() =
        object : TestGrammar() {
            val power = inOrder(ref { expr }, str(" ^ "), ref { expr }).map(::Power.asBinary())
            val paren = inOrder(str("("), ref { expr }, str(")")).map { (_, it, _) -> it }
            override val expr: Parser<ASTNode> = oneOfWithPrecedence(
                power,
                paren.nestedPrecedence(),
                number
            )
        }.run {
            "1" shouldBeParsedAs "1"

            "1 ^ 2" shouldBeParsedAs "(1 ^ 2)"
            "1 ^ 2 ^ 3" shouldBeParsedAs "(1 ^ (2 ^ 3))"

            "(1)" shouldBeParsedAs "1"
            "((1))" shouldBeParsedAs "1"
            "(1) ^ (2)" shouldBeParsedAs "(1 ^ 2)"

            "(1 ^ 2) ^ 3" shouldBeParsedAs "((1 ^ 2) ^ 3)"
            "1 ^ (2 ^ 3)" shouldBeParsedAs "(1 ^ (2 ^ 3))"

            "(1 ^ 2) ^ 3 ^ 4" shouldBeParsedAs "((1 ^ 2) ^ (3 ^ 4))"
            "1 ^ (2 ^ 3) ^ 4" shouldBeParsedAs "(1 ^ ((2 ^ 3) ^ 4))"
            "1 ^ 2 ^ (3 ^ 4)" shouldBeParsedAs "(1 ^ (2 ^ (3 ^ 4)))"
        }
}

class LeftAndRightAssociativityTests : TestGrammar() {
    private val plus = inOrder(ref { expr }, str(" + "), ref { expr }).mapLeftAssoc(::Plus.asBinary())
    private val power = inOrder(ref { expr }, str(" ^ "), ref { expr }).map(::Power.asBinary())
    override val expr: Parser<ASTNode> = oneOfWithPrecedence(
        plus,
        power,
        number
    )

    @Test
    fun `it works`() {
        "123" shouldBeParsedAs "123"

        "1 + 2" shouldBeParsedAs "(1 + 2)"
        "1 + 2 + 3" shouldBeParsedAs "((1 + 2) + 3)"

        "1 ^ 2" shouldBeParsedAs "(1 ^ 2)"
        "1 ^ 2 ^ 3" shouldBeParsedAs "(1 ^ (2 ^ 3))"

        "1 ^ 2 + 3" shouldBeParsedAs "((1 ^ 2) + 3)"
        "1 + 2 ^ 3" shouldBeParsedAs "(1 + (2 ^ 3))"
        "1 ^ 2 + 3 ^ 4" shouldBeParsedAs "((1 ^ 2) + (3 ^ 4))"

        "1 ^ 2 + 3 + 4" shouldBeParsedAs "(((1 ^ 2) + 3) + 4)"
        "1 + 2 ^ 3 + 4" shouldBeParsedAs "((1 + (2 ^ 3)) + 4)"
        "1 + 2 + 3 ^ 4" shouldBeParsedAs "((1 + 2) + (3 ^ 4))"

        "1 + 2 ^ 3 + 4 ^ 5" shouldBeParsedAs "((1 + (2 ^ 3)) + (4 ^ 5))"
        "1 ^ 2 + 3 + 4 ^ 5" shouldBeParsedAs "(((1 ^ 2) + 3) + (4 ^ 5))"
        "1 ^ 2 + 3 ^ 4 + 5" shouldBeParsedAs "(((1 ^ 2) + (3 ^ 4)) + 5)"

        "1 + 2 ^ 3 ^ 4" shouldBeParsedAs "(1 + (2 ^ (3 ^ 4)))"
        "1 ^ 2 ^ 3 + 4" shouldBeParsedAs "((1 ^ (2 ^ 3)) + 4)"
    }
}

abstract class TestGrammar {
    val cache = OutputCache<ASTNode>()
    val number = oneOrMore(oneOf('0'..'9')).map { Number(it.joinToString("")) }
    abstract val expr: Parser<ASTNode>

    infix fun String.shouldBeParsedAs(expected: String) = parseWith(expr).toString() shouldEqual expected

    interface ASTNode

    class Number(private val value: String) : ASTNode {
        override fun toString() = value
    }

    class PreIncrement(private val expression: ASTNode) : ASTNode {
        override fun toString() = "++($expression)"
    }

    class PreDecrement(private val expression: ASTNode) : ASTNode {
        override fun toString() = "--($expression)"
    }

    class Field(private val expression: ASTNode, private val name: String) : ASTNode {
        override fun toString() = "($expression.$name)"
    }

    class Plus(private val left: ASTNode, private val right: ASTNode) : ASTNode {
        override fun toString() = "($left + $right)"
    }

    class Minus(private val left: ASTNode, private val right: ASTNode) : ASTNode {
        override fun toString() = "($left - $right)"
    }

    class Power(private val left: ASTNode, private val right: ASTNode) : ASTNode {
        override fun toString() = "($left ^ $right)"
    }

    class Colon(private val left: ASTNode, private val right: ASTNode) : ASTNode {
        override fun toString() = "($left : $right)"
    }

    class Or(private val left: ASTNode, private val right: ASTNode) : ASTNode {
        override fun toString() = "($left || $right)"
    }

    class And(private val left: ASTNode, private val right: ASTNode) : ASTNode {
        override fun toString() = "($left && $right)"
    }

    class AccessByIndex(private val left: ASTNode, private val right: ASTNode) : ASTNode {
        override fun toString() = "$left[$right]"
    }
}
