package parser4k

import org.junit.jupiter.api.Test

class OneOfTests {
    @Test fun `it works`() {
        val abParser = oneOf(str("a"), str("b"))

        // not enough input
        abParser.parse(Input("")) shouldEqual null
        abParser.parse(Input("a", offset = 1)) shouldEqual null
        abParser.parse(Input("b", offset = 1)) shouldEqual null

        // input mismatch
        abParser.parse(Input("c")) shouldEqual null

        // match
        abParser.parse(Input("ab")) shouldEqual Output("a", Input("ab", offset = 1))
        abParser.parse(Input("ba")) shouldEqual Output("b", Input("ba", offset = 1))
    }
}

class OneOfLongestTests {
    @Test fun `basic examples`() {
        val parser = oneOfLongest(str("ab"), str("abc"))

        // not enough input
        parser.parse(Input("")) shouldEqual null
        parser.parse(Input("a", offset = 1)) shouldEqual null

        // input mismatch
        parser.parse(Input("b")) shouldEqual null
        parser.parse(Input("c")) shouldEqual null

        // match
        parser.parse(Input("ab"))?.payload shouldEqual "ab"
        parser.parse(Input("abc"))?.payload shouldEqual "abc"
    }

    @Test fun `recursive example`() = object {
        val fooField = inOrder(ref { parser }, str(".foo")).map { it.value1 + it.value2 }
        val fooFunc = inOrder(ref { parser }, str(".foo()")).map { it.value1 + it.value2 }
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