package parser4k

import org.junit.jupiter.api.Test

class InOrderTests {
    @Test fun `it works`() {
        val abParser = inOrder(str("a"), str("b"))

        // not enough input
        abParser.invoke(Input("")) shouldEqual null
        abParser.invoke(Input("a")) shouldEqual null
        abParser.invoke(Input("ab", offset = 1)) shouldEqual null

        // input mismatch
        abParser.invoke(Input("foo")) shouldEqual null
        abParser.invoke(Input("aa")) shouldEqual null

        // match
        abParser.invoke(Input("ab__")) shouldEqual Output(List2("a", "b"), Input("ab__", offset = 2))
        abParser.invoke(Input("_ab_", offset = 1)) shouldEqual Output(List2("a", "b"), Input("_ab_", offset = 3))
        abParser.invoke(Input("__ab", offset = 2)) shouldEqual Output(List2("a", "b"), Input("__ab", offset = 4))

        // match lists
        inOrder(str("a")).invoke(Input("ab")) shouldEqual Output(
            listOf("a"),
            Input("ab", offset = 1)
        )
        inOrder("abcdefghk".map { str(it.toString()) }).invoke(Input("abcdefghk")) shouldEqual Output(
            listOf("a", "b", "c", "d", "e", "f", "g", "h", "k"),
            Input("abcdefghk", offset = 9)
        )

        // match typed lists
        inOrder(str("a"), str("b")).invoke(Input("ab")) shouldEqual Output(
            List2("a", "b"),
            Input("ab", offset = 2)
        )
        inOrder(str("a"), str("b"), str("c")).invoke(Input("abc")) shouldEqual Output(
            List3("a", "b", "c"),
            Input("abc", offset = 3)
        )
        inOrder(str("a"), str("b"), str("c"), str("d")).invoke(Input("abcd")) shouldEqual Output(
            List4("a", "b", "c", "d"),
            Input("abcd", offset = 4)
        )
        inOrder(str("a"), str("b"), str("c"), str("d"), str("e")).invoke(Input("abcde")) shouldEqual Output(
            List5("a", "b", "c", "d", "e"),
            Input("abcde", offset = 5)
        )
        inOrder(str("a"), str("b"), str("c"), str("d"), str("e"), str("f")).invoke(Input("abcdef")) shouldEqual Output(
            List6("a", "b", "c", "d", "e", "f"),
            Input("abcdef", offset = 6)
        )
        inOrder(str("a"), str("b"), str("c"), str("d"), str("e"), str("f"), str("g")).invoke(Input("abcdefg")) shouldEqual Output(
            List7("a", "b", "c", "d", "e", "f", "g"),
            Input("abcdefg", offset = 7)
        )
        inOrder(str("a"), str("b"), str("c"), str("d"), str("e"), str("f"), str("g"), str("h")).invoke(Input("abcdefgh")) shouldEqual Output(
            List8("a", "b", "c", "d", "e", "f", "g", "h"),
            Input("abcdefgh", offset = 8)
        )
    }
}