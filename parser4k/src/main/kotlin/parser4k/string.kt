package parser4k

fun char(char: Char): Parser<Char> = Parser { input ->
    input.run {
        if (offset < value.length && value[offset] == char) Output(char, copy(offset = offset + 1))
        else null
    }
}

fun anyCharExcept(vararg chars: Char): Parser<Char> = Parser { input ->
    input.run {
        if (offset == value.length || value[offset] in chars) null
        else Output(value[offset], copy(offset = offset + 1))
    }
}

fun str(s: String) = Parser { input ->
    input.run {
        val newOffset = offset + s.length
        if (newOffset > value.length) null
        else {
            val token = value.substring(offset, newOffset)
            if (token == s) Output(token, copy(offset = newOffset)) else null
        }
    }
}
