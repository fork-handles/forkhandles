package parser4k

import java.util.*

fun oneOf(vararg chars: Char): Parser<Char> = oneOf(chars.map { char(it) })

fun oneOf(vararg charRanges: CharRange): Parser<Char> = oneOf(charRanges.map { oneOf(it) })

fun oneOf(vararg strings: String): Parser<String> = oneOf(strings.map { str(it) })

fun <T> oneOf(vararg parsers: Parser<T>): OneOf<T> = oneOf(parsers.toList())

fun <T> oneOf(parsers: Iterable<Parser<T>>): OneOf<T> = OneOf(parsers)

class OneOf<out T>(val parsers: Iterable<Parser<T>>): Parser<T> {
    override fun parse(input: Input): Output<T>? {
        parsers.forEach { parser ->
            val output = parser.parse(input)
            if (output != null) return output
        }
        return null
    }
}

fun oneOf(charRange: CharRange): Parser<Char> = object : Parser<Char> {
    override fun parse(input: Input): Output<Char>? = input.run {
        if (offset == value.length || value[offset] !in charRange) null
        else Output(value[offset], copy(offset = offset + 1))
    }
}

fun CharRange.except(vararg chars: Char): Parser<Char> = oneOf(this).except(chars.map { char(it) })

fun <T> Parser<T>.except(vararg chars: Char): Parser<T> = except(chars.map { char(it) })

fun <T> Parser<T>.except(parsers: Iterable<Parser<*>>): Parser<T> = object : Parser<T> {
    override fun parse(input: Input): Output<T>? {
        val output = this@except.parse(input) ?: return null
        if (parsers.any { it.parse(input) != null }) return null
        return output
    }
}

fun <T> oneOfLongest(vararg parsers: Parser<T>): Parser<T> = nonRecursive(object : Parser<T> {
    override fun parse(input: Input) =
        parsers.mapNotNull { it.parse(input) }.maxBy { it.nextInput.offset }
})

fun <T> oneOfWithPrecedence(vararg parsers: Parser<T>): Parser<T> = oneOfWithPrecedence(parsers.toList())

fun <T> oneOfWithPrecedence(parsers: List<Parser<T>>) = object : Parser<T> {
    val stack: LinkedList<Parser<T>> = LinkedList()

    override fun parse(input: Input): Output<T>? {
        val prevParser = stack.peek()
        val prevParserIndex = parsers.indexOf(prevParser)
        val isNestedPrecedence = prevParser == null || prevParser is NestedPrecedence
        val filteredParsers =
            if (isNestedPrecedence) parsers
            else parsers.drop(parsers.indexOf(prevParser))

        filteredParsers.forEach { parser ->
            stack.push(parser)
            val parserIndex = parsers.indexOf(parser)
            val output =
                if (prevParserIndex < parserIndex || (isNestedPrecedence && prevParser != parser)) {
                    parser.parse(input.copy(leftPayload = null))
                } else {
                    parser.parse(input)
                }
            stack.pop()
            if (output != null) return output
        }
        return null
    }
}

fun <T> Parser<T>.nestedPrecedence() = NestedPrecedence(this)

class NestedPrecedence<T>(private val parser: Parser<T>) : Parser<T> {
    override fun parse(input: Input) = parser.parse(input)
}


interface OneOfExtensions {
    infix fun Char.or(that: Char): OneOf<Char> = oneOf(char(this), char(that))
    infix fun Char.or(that: CharRange): OneOf<Char> = oneOf(char(this), oneOf(that))
    infix fun Char.or(that: String): OneOf<String> = oneOf(char(this).map { it.toString() }, str(that))
    infix fun <T> Char.or(that: Parser<T>): OneOf<Any?> = oneOf(char(this), that)
    infix fun OneOf<Char>.or(that: Char): OneOf<Char> = oneOf(parsers + char(that))
    @Suppress("INAPPLICABLE_JVM_NAME") @JvmName("orChar")
    infix fun <T> OneOf<T>.or(that: Char): OneOf<Any?> = oneOf(parsers + char(that))
    infix fun <T> Parser<T>.or(that: Char): OneOf<Any?> = oneOf(this, char(that))

    infix fun CharRange.or(that: Char): OneOf<Char> = oneOf(oneOf(this), char(that))
    infix fun CharRange.or(that: CharRange): OneOf<Char> = oneOf(oneOf(this), oneOf(that))
    infix fun CharRange.or(that: String): OneOf<String> = oneOf(oneOf(this).map { it.toString() }, str(that))
    infix fun <T> CharRange.or(that: Parser<T>): OneOf<Any?> = oneOf(oneOf(this), that)
    infix fun OneOf<CharRange>.or(that: CharRange): OneOf<Any> = oneOf(parsers + oneOf(that))
    @Suppress("INAPPLICABLE_JVM_NAME") @JvmName("orCharRange")
    infix fun <T> OneOf<T>.or(that: CharRange): OneOf<Any?> = oneOf(parsers + oneOf(that))
    infix fun <T> Parser<T>.or(that: CharRange): OneOf<Any?> = oneOf(this, oneOf(that))

    infix fun String.or(that: Char): OneOf<String> = oneOf(str(this), char(that).map { it.toString() })
    infix fun String.or(that: CharRange): OneOf<String> = oneOf(str(this), oneOf(that).map { it.toString() })
    infix fun String.or(that: String): OneOf<String> = oneOf(str(this), str(that))
    infix fun <T> String.or(that: Parser<T>): OneOf<Any?> = oneOf(str(this), that)
    infix fun OneOf<String>.or(that: String): OneOf<String> = oneOf(parsers + str(that))
    @Suppress("INAPPLICABLE_JVM_NAME") @JvmName("orString")
    infix fun <T> OneOf<T>.or(that: String): OneOf<Any?> = oneOf(parsers + str(that))
    infix fun <T> Parser<T>.or(that: String): OneOf<Any?> = oneOf(this, str(that))

    infix fun <T> OneOf<T>.or(that: OneOf<T>): OneOf<T> = oneOf(parsers + that)
    infix fun <T> OneOf<T>.or(that: Parser<T>): OneOf<T> = oneOf(parsers + that)
    infix fun <T> Parser<T>.or(that: Parser<T>): OneOf<T> = oneOf(this, that)
}
