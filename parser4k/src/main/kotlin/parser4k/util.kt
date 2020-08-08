package parser4k


fun oneOf(charRange: CharRange): Parser<String> = oneOf(charRange.map { str(it.toString()) })

fun oneOf(vararg chars: Char): Parser<String> = oneOf(chars.map { str(it.toString()) })

fun noneOf(vararg chars: Char): Parser<String> = object : Parser<String> {
    val parser: Parser<String> = oneOf(*chars)

    override fun parse(input: Input): Output<String>? =
        if (parser.parse(input) != null) null
        else Output(
            payload = input.value[input.offset].toString(),
            nextInput = input.copy(offset = input.offset + 1)
        )
}

fun <T1, T3, R> ((T1, T3) -> R).asBinary() = { list: List3<T1, *, T3> ->
    this(list.value1, list.value3)
}

fun <T> Parser<*>.skip(): Parser<T> = object : Parser<T> {
    override fun parse(input: Input): Output<T>? {
        val (_, nextInput) = this@skip.parse(input) ?: return null
        @Suppress("UNCHECKED_CAST")
        return Output(null as T, nextInput)
    }
}

fun <T> String.parseWith(parser: Parser<T>): T {
    val output = parser.parse(Input(this)) ?: throw NoMatchingParsers(this)
    if (output.nextInput.offset < output.nextInput.value.length) throw InputIsNotConsumed(output)
    return output.payload
}

sealed class ParsingError(override val message: String) : RuntimeException(message)

class NoMatchingParsers(override val message: String) : ParsingError(message)

class InputIsNotConsumed(override val message: String) : ParsingError(message) {
    constructor(output: Output<*>) : this(
        "\n" + // start new line after "parser4k.InputIsNotConsumed: "
        "${output.nextInput.value}\n" +
        " ".repeat(output.nextInput.offset) + "^\n" +
        "payload = ${output.payload}"
    )
}

operator fun <T> List<T>.component6(): T = this[5]
operator fun <T> List<T>.component7(): T = this[6]
operator fun <T> List<T>.component8(): T = this[7]
