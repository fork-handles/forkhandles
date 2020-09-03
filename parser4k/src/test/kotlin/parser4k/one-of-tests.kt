package parser4k

import dev.forkhandles.tuples.val1
import dev.forkhandles.tuples.val2
import org.junit.jupiter.api.Test

class OneOfTests {
    private val parser = oneOf(str("a"), str("b"))

    @Test fun `not enough input`() {
        parser.parse(Input("")) shouldEqual null
        parser.parse(Input("a", offset = 1)) shouldEqual null
        parser.parse(Input("b", offset = 1)) shouldEqual null
        parser.parse(Input("ab", offset = 2)) shouldEqual null
    }

    @Test fun `input mismatch`() {
        parser.parse(Input("c")) shouldEqual null
    }

    @Test fun `input match`() {
        parser.parse(Input("ab")) shouldEqual Output("a", Input("ab", offset = 1))
        parser.parse(Input("ba")) shouldEqual Output("b", Input("ba", offset = 1))
    }

    @Test fun `don't attempt to parse if input is already consumed`() {
        val logEvents = ArrayList<ParsingEvent>()
        val log = ParsingLog { logEvents.add(it) }
        val parser = oneOf(str("abc").with("abc", log))

        parser.parse(Input("abc", offset = 3))

        logEvents shouldEqual emptyList<ParsingEvent>()
    }
}

class OneOfCharRangeTests {
    private val parser = oneOf('a'..'z').except('x')

    @Test fun `not enough input`() {
        parser.parse(Input("")) shouldEqual null
        parser.parse(Input("a", offset = 1)) shouldEqual null
    }

    @Test fun `input mismatch`() {
        parser.parse(Input("_")) shouldEqual null
        parser.parse(Input("x")) shouldEqual null
    }

    @Test fun `input match`() {
        parser.parse(Input("ab")) shouldEqual Output('a', Input("ab", offset = 1))
        parser.parse(Input("ba")) shouldEqual Output('b', Input("ba", offset = 1))
        parser.parse(Input("az", offset = 1)) shouldEqual Output('z', Input("az", offset = 2))
    }
}

class OneOfLongestTests {
    val parser = oneOfLongest(str("ab"), str("abc"))

    @Test fun `not enough input`() {
        parser.parse(Input("")) shouldEqual null
        parser.parse(Input("a", offset = 1)) shouldEqual null
    }

    @Test fun `input mismatch`() {
        parser.parse(Input("b")) shouldEqual null
        parser.parse(Input("c")) shouldEqual null
    }

    @Test fun `input match`() {
        parser.parse(Input("ab"))?.payload shouldEqual "ab"
        parser.parse(Input("abc"))?.payload shouldEqual "abc"
    }

    @Test fun `recursive example`() = object {
        val fooField = inOrder(ref { parser }, str(".foo")).map { it.val1 + it.val2 }
        val fooFunc = inOrder(ref { parser }, str(".foo()")).map { it.val1 + it.val2 }
        val parser: Parser<String> = oneOf(
            oneOfLongest(
                fooField, // when fooFunc calls `parser` recursively it should avoid using fooField
                fooFunc
            ),
            str("1")
        )
    }.run {
        parser.parse(Input("1.foo"))?.payload shouldEqual "1.foo"
        parser.parse(Input("1.foo()"))?.payload shouldEqual "1.foo()"
    }
}