package parser4k

class InOrder<T>(val parsers: List<Parser<T>>) : Parser<List<T>> {
    private val allParsers = parsers.mapIndexed { index, parser ->
        if (index == 0) nonRecursive(parser) else parser
    }

    override fun parse(input: Input): Output<List<T>>? {
        val payload = ArrayList<T>(allParsers.size)
        var nextInput = input
        allParsers.forEach { parser ->
            val output = parser.parse(nextInput) ?: return null
            nextInput = output.nextInput
            payload.add(output.payload)
        }
        return Output(payload, nextInput)
    }
}

fun <T> inOrder(parsers: List<Parser<T>>) = InOrder(parsers)

fun <T> inOrder(vararg parsers: Parser<T>) = inOrder(parsers.toList())


interface InOrderExtensions {
    operator fun <T> Char.plus(that: Char) = inOrder(char(this), char(that))
    operator fun <T> Char.plus(that: CharRange) = inOrder(char(this), oneOf(that))
    operator fun <T> Char.plus(that: String) = inOrder(char(this), str(that))
    operator fun <T> Char.plus(that: Parser<T>) = inOrder(char(this), that)

    operator fun <T> CharRange.plus(that: Char) = inOrder(oneOf(this), char(that))
    operator fun <T> CharRange.plus(that: CharRange) = inOrder(oneOf(this), oneOf(that))
    operator fun <T> CharRange.plus(that: String) = inOrder(oneOf(this), str(that))
    operator fun <T> CharRange.plus(that: Parser<T>) = inOrder(oneOf(this), that)

    // Can't have String.plus extension functions because they're shadowed by stdlib

    operator fun <T> Parser<T>.plus(that: Char) = inOrder(this, char(that))
    operator fun <T> Parser<T>.plus(that: CharRange) = inOrder(this, oneOf(that))
    operator fun <T> Parser<T>.plus(that: String) = inOrder(this, str(that))
    operator fun <T> Parser<T>.plus(that: Parser<T>) = inOrder(this, that)

    operator fun <T1, T2> InOrder2<T1, T2>.plus(that: Char) = InOrder3(parser1, parser2, char(that))
    operator fun <T1, T2, T3> InOrder2<T1, T2>.plus(parser3: Parser<T3>) = InOrder3(parser1, parser2, parser3)
    operator fun <T1, T2, T3, T4> InOrder3<T1, T2, T3>.plus(parser4: Parser<T4>) = InOrder4(parser1, parser2, parser3, parser4)
    operator fun <T1, T2, T3, T4, T5> InOrder4<T1, T2, T3, T4>.plus(parser5: Parser<T5>) = InOrder5(parser1, parser2, parser3, parser4, parser5)
    operator fun <T1, T2, T3, T4, T5, T6> InOrder5<T1, T2, T3, T4, T5>.plus(parser6: Parser<T6>) = InOrder6(parser1, parser2, parser3, parser4, parser5, parser6)
    operator fun <T1, T2, T3, T4, T5, T6, T7> InOrder6<T1, T2, T3, T4, T5, T6>.plus(parser7: Parser<T7>) = InOrder7(parser1, parser2, parser3, parser4, parser5, parser6, parser7)
    operator fun <T1, T2, T3, T4, T5, T6, T7, T8> InOrder7<T1, T2, T3, T4, T5, T6, T7>.plus(parser8: Parser<T8>) = InOrder8(parser1, parser2, parser3, parser4, parser5, parser6, parser7, parser8)
}
