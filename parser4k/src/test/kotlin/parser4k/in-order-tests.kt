package parser4k

import dev.forkhandles.tuples.Tuple3
import dev.forkhandles.tuples.Tuple4
import dev.forkhandles.tuples.Tuple5
import dev.forkhandles.tuples.Tuple6
import dev.forkhandles.tuples.Tuple7
import dev.forkhandles.tuples.Tuple8
import org.junit.jupiter.api.Test

class InOrderTests {
    private val abParser = inOrder(str("a"), str("b"))

    @Test
    fun `not enough input`() {
        abParser.parse(Input("")) shouldEqual null
        abParser.parse(Input("a")) shouldEqual null
        abParser.parse(Input("ab", offset = 1)) shouldEqual null
    }

    @Test
    fun `input mismatch`() {
        abParser.parse(Input("foo")) shouldEqual null
        abParser.parse(Input("aa")) shouldEqual null
    }

    @Test
    fun `input match`() {
        abParser.parse(Input("ab__")) shouldEqual Output(Pair("a", "b"), Input("ab__", offset = 2))
        abParser.parse(Input("_ab_", offset = 1)) shouldEqual Output(Pair("a", "b"), Input("_ab_", offset = 3))
        abParser.parse(Input("__ab", offset = 2)) shouldEqual Output(Pair("a", "b"), Input("__ab", offset = 4))
    }

    @Test
    fun `input match with list as payload`() {
        inOrder(str("a")).parse(Input("ab")) shouldEqual Output(
            payload = listOf("a"),
            nextInput = Input("ab", offset = 1)
        )
        inOrder("abcdefghk".map { str(it.toString()) }).parse(Input("abcdefghk")) shouldEqual Output(
            payload = listOf("a", "b", "c", "d", "e", "f", "g", "h", "k"),
            nextInput = Input("abcdefghk", offset = 9)
        )
    }

    @Test
    fun `input match with tuple as payload`() {
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
