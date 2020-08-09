package parser4k.examples.json

import dev.forkhandles.tuples.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.fail
import parser4k.*
import parser4k.commonparsers.joinedWith
import parser4k.examples.json.JsonParser.parse
import java.io.File

/**
 * Based on https://www.json.org/json-en.html
 */
private object JsonParser {
    private val ws = zeroOrMore(oneOf('\u0020', '\u000A', '\u000D', '\u0009'))
    private val sign = oneOf("+", "-", "")
    private val oneNine = oneOf('1'..'9')
    private val digit = oneOf(char('0'), oneNine)
    private val digits = oneOrMore(digit).map { it.joinToString("") }
    private val exponent = optional(inOrder(oneOf('E', 'e'), sign, digits)).map { it?.joinToString() ?: "" }
    private val fraction = optional(inOrder(char('.'), digits)).map { it?.joinToString() ?: "" }
    private val integer = oneOf(
        inOrder(sign, oneNine, digits).map { it.joinToString() },
        inOrder(sign, digit).map { it.joinToString() },
        inOrder(oneNine, digits).map { it.joinToString() },
        digit.map { it.toString() }
    )
    private val number = inOrder(integer, fraction, exponent)
        .map { (integer, fraction, exponent) ->
            @Suppress("USELESS_CAST") // IntelliJ is wrong
            if (fraction.isEmpty() && exponent.isEmpty()) integer.toInt()
            else (integer + fraction + exponent).toDouble() as Number
        }

    private val hex = oneOf(digit, oneOf('A'..'F'), oneOf('a'..'f'))
    private val escape = oneOf(
        oneOf('"', '\\', '/', 'b', 'f', 'n', 'r', 't').map { it.toEscapedChar() },
        inOrder(char('u'), hex, hex, hex, hex).skipFirst().map { it.joinToString().toInt(16).toChar() }
    )
    private val characters = zeroOrMore(oneOf(
        oneOf(0x0020.toChar()..0x10FFFF.toChar()).except('"', '\\'),
        inOrder(char('\\'), escape).skipFirst()
    ))
    private val string =
        inOrder(char('"'), characters, char('"'))
            .skipWrapper().map { it.joinToString("") }

    private val member: Parser<Pair<Any, Any?>> =
        inOrder(ws, string, ws, str(":"), ref { element })
            .map { (_, id, _, _, element) -> Pair(id, element) }

    private val obj = oneOf(
        inOrder(str("{"), ws, str("}")).map { emptyMap<Any, Any>() },
        inOrder(str("{"), member.joinedWith(","), str("}")).skipWrapper().map { it.toMap() }
    )

    private val array = oneOf(
        inOrder(str("["), ws, str("]")).map { emptyList<Any>() },
        inOrder(str("["), ref { element }.joinedWith(","), str("]")).skipWrapper()
    )

    private val value: Parser<Any?> = oneOf(
        obj,
        array,
        string,
        number,
        str("true").map { true },
        str("false").map { false },
        str("null").map { null }
    )
    private val element = inOrder(ws, value, ws).skipWrapper()

    private val json = element

    fun parse(s: String) = s.parseWith(json)

    private fun Char.toEscapedChar() =
        when (this) {
            'b' -> '\b'
            'f' -> '\u000c'
            'n' -> '\n'
            'r' -> '\r'
            't' -> '\t'
            else -> this
        }
}

private fun <T1, T2> Tuple2<T1, T2>.joinToString(): String = val1.toString() + val2
private fun <T1, T2, T3> Tuple3<T1, T2, T3>.joinToString(): String = val1.toString() + val2 + val3
private fun <T1, T2, T3, T4> Tuple4<T1, T2, T3, T4>.joinToString(): String = val1.toString() + val2 + val3 + val4

class JsonParserTests {
    private val emptyObject = emptyMap<String, Any>()
    private val emptyList = emptyList<Any>()

    @Test fun `null`() {
        parse("null") shouldEqual null
    }

    @Test fun booleans() {
        parse("true") shouldEqual true
        parse("false") shouldEqual false
    }

    @Test fun integers() {
        parse("0") shouldEqual 0
        parse("+1") shouldEqual 1
        parse("-1") shouldEqual -1
        parse("123") shouldEqual 123
        parse("+123") shouldEqual 123
        parse("-123") shouldEqual -123
        parse("1234567890") shouldEqual 1234567890
    }

    @Test fun `floating points`() {
        parse("123.456") shouldEqual 123.456
        parse("+123.456") shouldEqual 123.456
        parse("-123.456") shouldEqual -123.456
        parse("-123.456") shouldEqual -123.456
        parse("-9876.543210") shouldEqual -9876.543210
        parse("1e1") shouldEqual 1e1
        parse("1e-1") shouldEqual 1e-1
        parse("1e00") shouldEqual 1e00
        parse("0.1e1") shouldEqual 0.1e1
        parse("2e+00") shouldEqual 2e+00
        parse("2e-00") shouldEqual 2e-00
        parse("0.123456789e-12") shouldEqual 0.123456789e-12
        parse("1.234567890E+34") shouldEqual 1.234567890E+34
        parse("23456789012E66") shouldEqual 23456789012E66
    }

    @Test fun strings() {
        fun String.quoted() = "\"$this\""

        parse("".quoted()) shouldEqual ""
        parse(" ".quoted()) shouldEqual " "
        parse("""\"""".quoted()) shouldEqual "\""
        parse("""\\""".quoted()) shouldEqual "\\"
        parse("""\b""".quoted()) shouldEqual "\b"
        parse("""\f""".quoted()) shouldEqual "\u000c"
        parse("""\n""".quoted()) shouldEqual "\n"
        parse("""\r""".quoted()) shouldEqual "\r"
        parse("""\t""".quoted()) shouldEqual "\t"
        parse("""\u0123\u4567\u89AB\uCDEF\uabcd\uef4A""".quoted()) shouldEqual "\u0123\u4567\u89AB\uCDEF\uabcd\uef4A"
        parse("abcdefghijklmnopqrstuvwyz".quoted()) shouldEqual "abcdefghijklmnopqrstuvwyz"
        parse("ABCDEFGHIJKLMNOPQRSTUVWYZ".quoted()) shouldEqual "ABCDEFGHIJKLMNOPQRSTUVWYZ"
        parse("0123456789".quoted()) shouldEqual "0123456789"
        parse("`1~!@#$%^&*()_+-={':[,]}|;.</>?".quoted()) shouldEqual "`1~!@#$%^&*()_+-={':[,]}|;.</>?"
    }

    @Test fun arrays() {
        parse("[]") shouldEqual emptyList
        parse("[1, 2, 3]") shouldEqual listOf(1, 2, 3)
        parse("[[1, 2, 3], [4, 5, [6]]]") shouldEqual listOf(listOf(1, 2, 3), listOf(4, 5, listOf(6)))
        parse("""[1,2 , 3
            |
            |,
            |
            |4 , 5        ,          6           ,7        ]
            |""".trimMargin()) shouldEqual listOf(1, 2, 3, 4, 5, 6, 7)
    }

    @Test fun objects() {
        parse("""{}""") shouldEqual emptyObject
        parse("""{ "foo": 123 }""") shouldEqual mapOf("foo" to 123)
        parse("""{ "foo": 123, "bar": "woof" }""") shouldEqual mapOf("foo" to 123, "bar" to "woof")
        parse("""{ "foo": { "bar": 123 }}""") shouldEqual mapOf("foo" to mapOf("bar" to 123))
        parse("""{ "foo": [1,2,3] }""") shouldEqual mapOf("foo" to listOf(1, 2, 3))
        parse("""[{ "foo": 123 }]""") shouldEqual listOf(mapOf("foo" to 123))
        parse("""{"jsontext": "{\"object with 1 member\":[\"array with 1 element\"]}"}""") shouldEqual
            mapOf("jsontext" to """{"object with 1 member":["array with 1 element"]}""")
    }

    @Test fun `test cases from json-dot-org`() {
        // These tests cases were downloaded from https://www.json.org/JSON_checker
        // excluding fail1.json because strings are valid root elements according to json grammar,
        // excluding fail18.json because there is no official maximum depth for nested arrays.
        val testCases = File("src/test/kotlin/parser4k/examples/json/testcases").listFiles()
            ?: fail("Couldn't find files with json test cases")

        testCases.forEach { file ->
            try {
                parse(file.readText())
                if (file.name.startsWith("fail")) fail("Expected failure: ${file.name}")
            } catch (e: Exception) {
                if (file.name.startsWith("pass")) throw e
            }
        }
    }
}