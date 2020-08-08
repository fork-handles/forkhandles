package parser4k

import dev.forkhandles.tuples.*
import org.junit.jupiter.api.Test

class InOrderTests {
    @Test fun `it works`() {
        val abParser = inOrder(str("a"), str("b"))

        // not enough input
        abParser.parse(Input("")) shouldEqual null
        abParser.parse(Input("a")) shouldEqual null
        abParser.parse(Input("ab", offset = 1)) shouldEqual null

        // input mismatch
        abParser.parse(Input("foo")) shouldEqual null
        abParser.parse(Input("aa")) shouldEqual null

        // match
        abParser.parse(Input("ab__")) shouldEqual Output(Tuple2("a", "b"), Input("ab__", offset = 2))
        abParser.parse(Input("_ab_", offset = 1)) shouldEqual Output(Tuple2("a", "b"), Input("_ab_", offset = 3))
        abParser.parse(Input("__ab", offset = 2)) shouldEqual Output(Tuple2("a", "b"), Input("__ab", offset = 4))

        // match lists
        inOrder(str("a")).parse(Input("ab")) shouldEqual Output(
            listOf("a"),
            Input("ab", offset = 1)
        )
        inOrder("abcdefghk".map { str(it.toString()) }).parse(Input("abcdefghk")) shouldEqual Output(
            listOf("a", "b", "c", "d", "e", "f", "g", "h", "k"),
            Input("abcdefghk", offset = 9)
        )

        // match typed lists
        inOrder(str("a"), str("b")).parse(Input("ab")) shouldEqual Output(
            Tuple2("a", "b"),
            Input("ab", offset = 2)
        )
        inOrder(str("a"), str("b"), str("c")).parse(Input("abc")) shouldEqual Output(
            Tuple3("a", "b", "c"),
            Input("abc", offset = 3)
        )
        inOrder(str("a"), str("b"), str("c"), str("d")).parse(Input("abcd")) shouldEqual Output(
            Tuple4("a", "b", "c", "d"),
            Input("abcd", offset = 4)
        )
        inOrder(str("a"), str("b"), str("c"), str("d"), str("e")).parse(Input("abcde")) shouldEqual Output(
            Tuple5("a", "b", "c", "d", "e"),
            Input("abcde", offset = 5)
        )
        inOrder(str("a"), str("b"), str("c"), str("d"), str("e"), str("f")).parse(Input("abcdef")) shouldEqual Output(
            Tuple6("a", "b", "c", "d", "e", "f"),
            Input("abcdef", offset = 6)
        )
        inOrder(str("a"), str("b"), str("c"), str("d"), str("e"), str("f"), str("g")).parse(Input("abcdefg")) shouldEqual Output(
            Tuple7("a", "b", "c", "d", "e", "f", "g"),
            Input("abcdefg", offset = 7)
        )
        inOrder(str("a"), str("b"), str("c"), str("d"), str("e"), str("f"), str("g"), str("h")).parse(Input("abcdefgh")) shouldEqual Output(
            Tuple8("a", "b", "c", "d", "e", "f", "g", "h"),
            Input("abcdefgh", offset = 8)
        )
    }
}