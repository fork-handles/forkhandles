package parser4k

fun <T> InOrder<T>.mapLeftAssoc(transform: (List<T>) -> T) = object : Parser<T> {
    private val leftParser = nonRecursive(parsers.first())
    private val midParsers = parsers.drop(1).dropLast(1)
    private val rightParser = parsers.last()

    override fun parse(input: Input): Output<T>? {
        if (input.leftPayload == RightRecursionMarker) return null

        @Suppress("UNCHECKED_CAST")
        val leftOutput =
            if (input.leftPayload == null) leftParser.parse(input) ?: return null
            else Output(input.leftPayload as T, input.copy(leftPayload = null))
        val midOutput = InOrder(midParsers).parse(leftOutput.nextInput) ?: return null
        val rightOutput = rightParser.parse(midOutput.nextInput.copy(leftPayload = RightRecursionMarker)) ?: return null

        val payload = transform(listOf(leftOutput.payload) + midOutput.payload + rightOutput.payload)
        val nextInput = rightOutput.nextInput.copy(leftPayload = payload)
        return leftParser.parse(nextInput) ?: return Output(payload, nextInput)
    }
}

private object RightRecursionMarker {
    override fun toString() = "RightRecursionMarker"
}
