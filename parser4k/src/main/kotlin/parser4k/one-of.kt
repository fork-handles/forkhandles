package parser4k

import java.util.*


fun <T> oneOf(vararg parsers: Parser<T>): Parser<T> = oneOf(parsers.toList())

fun <T> oneOf(parsers: List<Parser<T>>) = object : Parser<T> {
    override fun parse(input: Input): Output<T>? {
        parsers.forEach { parser ->
            val output = parser.parse(input)
            if (output != null) return output
        }
        return null
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
