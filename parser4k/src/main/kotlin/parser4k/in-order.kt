package parser4k

class InOrder<T>(val parsers: List<Parser<T>>) : Parser<List<T>> {
    private val allParsers = parsers.mapIndexed { index, parser ->
        if (index == 0) nonRecursive(parser) else parser
    }

    override fun invoke(input: Input): Output<List<T>>? {
        val payload = ArrayList<T>(allParsers.size)
        var nextInput = input
        allParsers.forEach { parser ->
            val output = parser.invoke(nextInput) ?: return null
            nextInput = output.nextInput
            payload.add(output.payload)
        }
        return Output(payload, nextInput)
    }
}

fun <T> inOrder(parsers: List<Parser<T>>) = InOrder(parsers)

fun <T> inOrder(vararg parsers: Parser<T>) = inOrder(parsers.toList())
