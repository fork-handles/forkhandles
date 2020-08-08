package parser4k

import java.util.*

fun oneOf(vararg chars: Char): Parser<Char> = oneOf(chars.map { char(it) })

fun oneOf(vararg strings: String): Parser<String> = oneOf(strings.map { str(it) })

fun <T> oneOf(vararg parsers: Parser<T>): Parser<T> = oneOf(parsers.toList())

fun <T> oneOf(parsers: Iterable<Parser<T>>) = object : Parser<T> {
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
