package parser4k

fun char(char: Char): Parser<Char> = object : Parser<Char> {
    override fun parse(input: Input): Output<Char>? = input.run {
        if (offset < value.length && value[offset] == char) Output(char, copy(offset = offset + 1))
        else null
    }
}

fun anyCharExcept(vararg chars: Char): Parser<Char> = object : Parser<Char> {
    private val oneOfChars: Parser<Char> = oneOf(*chars)

    override fun parse(input: Input): Output<Char>? = input.run {
        if (offset == value.length || oneOfChars.parse(input) != null) null
        else Output(value[offset], copy(offset = offset + 1))
    }
}

fun str(s: String) = object : Parser<String> {
    override fun parse(input: Input): Output<String>? = input.run {
        val newOffset = offset + s.length
        if (newOffset > value.length) null
        else {
            val token = value.substring(offset, newOffset)
            if (token == s) Output(token, copy(offset = newOffset)) else null
        }
    }
}
