package parser4k

import org.junit.jupiter.api.Test

class OneOfTests {
    @Test fun `it works`() {
        val abParser = oneOf(str("a"), str("b"))

        // not enough input
        abParser.invoke(Input("")) shouldEqual null
        abParser.invoke(Input("a", offset = 1)) shouldEqual null
        abParser.invoke(Input("b", offset = 1)) shouldEqual null

        // input mismatch
        abParser.invoke(Input("c")) shouldEqual null

        // match
        abParser.invoke(Input("ab")) shouldEqual Output("a", Input("ab", offset = 1))
        abParser.invoke(Input("ba")) shouldEqual Output("b", Input("ba", offset = 1))
    }
}

class OneOfLongestTests {
    @Test fun `basic examples`() {
        val parser = oneOfLongest(str("ab"), str("abc"))

        // not enough input
        parser.invoke(Input("")) shouldEqual null
        parser.invoke(Input("a", offset = 1)) shouldEqual null

        // input mismatch
        parser.invoke(Input("b")) shouldEqual null
        parser.invoke(Input("c")) shouldEqual null

        // match
        parser.invoke(Input("ab"))?.payload shouldEqual "ab"
        parser.invoke(Input("abc"))?.payload shouldEqual "abc"
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
        parser.invoke(Input("1.foo"))?.payload shouldEqual "1.foo"
        parser.invoke(Input("1.foo()"))?.payload shouldEqual "1.foo()"
    }
}