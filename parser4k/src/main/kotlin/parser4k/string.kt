package parser4k

fun str(s: String) = object : Parser<String> {
    override fun invoke(input: Input): Output<String>? = input.run {
        val newOffset = offset + s.length
        if (newOffset > value.length) null
        else {
            val token = value.substring(offset, newOffset)
            if (token == s) Output(token, copy(offset = newOffset)) else null
        }
    }
}
