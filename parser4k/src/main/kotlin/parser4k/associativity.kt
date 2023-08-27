package parser4k

fun <T> InOrder<T>.mapLeftAssoc(transform: (List<T>) -> T) = object : Parser<T> {
    private val leftParser = nonRecursive(parsers.first())
    private val midParsers = parsers.drop(1).dropLast(1)
    private val rightParser = parsers.last().withRightRecursionMarker()

    override fun parse(input: Input): Output<T>? {
        if (input.leftPayload == RightRecursionMarker) return null

        @Suppress("UNCHECKED_CAST")
        val leftOutput =
            if (input.leftPayload == null) leftParser.parse(input) ?: return null
            else Output(input.leftPayload as T, input.copy(leftPayload = null))
        val midOutput = InOrder(midParsers).parse(leftOutput.nextInput) ?: return null
        val rightOutput = rightParser.parse(midOutput.nextInput) ?: return null

        val payload = transform(listOf(leftOutput.payload) + midOutput.payload + rightOutput.payload)
        val output = leftParser.parse(rightOutput.nextInput.copy(leftPayload = payload))

        return if (output != null && output.nextInput.leftPayload == null) output
        else Output(payload, rightOutput.nextInput)
    }

    private fun <T> Parser<T>.withRightRecursionMarker() = Parser { input ->
        fun Input.setRightRecursionMarker() = copy(leftPayload = RightRecursionMarker)
        fun Input.clearRightRecursionMarker() = if (leftPayload == RightRecursionMarker) copy(leftPayload = null) else this

        val output = parse(input.setRightRecursionMarker())
        output?.copy(nextInput = output.nextInput.clearRightRecursionMarker())
    }
}

private data object RightRecursionMarker
