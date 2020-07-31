package parser4k

import org.junit.jupiter.api.Test

class RepeatTests {
    @Test fun `it works`() {
        // not enough input
        repeat(str("a"), atLeast = 1).invoke(Input("")) shouldEqual null
        repeat(str("a"), atLeast = 1).invoke(Input("a", offset = 1)) shouldEqual null
        repeat(str("a"), atLeast = 2).invoke(Input("a")) shouldEqual null
        oneOrMore(str("a")).invoke(Input("")) shouldEqual null

        // input mismatch
        repeat(str("a"), atLeast = 1).invoke(Input("b")) shouldEqual null
        oneOrMore(str("a")).invoke(Input("b")) shouldEqual null

        // match
        repeat(str("a")).invoke(Input("")) shouldEqual Output(emptyList<String>(), Input(""))
        repeat(str("a")).invoke(Input("b")) shouldEqual Output(emptyList<String>(), Input("b"))
        repeat(str("a")).invoke(Input("aaa")) shouldEqual Output(listOf("a", "a", "a"), Input("aaa", offset = 3))
        repeat(str("a"), atLeast = 1).invoke(Input("aaa")) shouldEqual Output(listOf("a", "a", "a"), Input("aaa", offset = 3))
        repeat(str("a"), atLeast = 2).invoke(Input("aaa")) shouldEqual Output(listOf("a", "a", "a"), Input("aaa", offset = 3))
        repeat(str("a"), atMost = 1).invoke(Input("aaa")) shouldEqual Output(listOf("a"), Input("aaa", offset = 1))
        repeat(str("a"), atMost = 2).invoke(Input("aaa")) shouldEqual Output(listOf("a", "a"), Input("aaa", offset = 2))
        repeat(str("a"), atMost = 3).invoke(Input("aaa")) shouldEqual Output(listOf("a", "a", "a"), Input("aaa", offset = 3))

        zeroOrMore(str("a")).invoke(Input("aaa")) shouldEqual Output(listOf("a", "a", "a"), Input("aaa", offset = 3))
        oneOrMore(str("a")).invoke(Input("aaa")) shouldEqual Output(listOf("a", "a", "a"), Input("aaa", offset = 3))

        optional(str("a")).invoke(Input("")) shouldEqual Output(null, Input(""))
        optional(str("a")).invoke(Input("a")) shouldEqual Output("a", Input("a", offset = 1))
        optional(str("a")).invoke(Input("aa")) shouldEqual Output("a", Input("aa", offset = 1))
    }
}