package parser4k

import org.junit.jupiter.api.Test

class StringTests {
    private val parser = str("foo")

    @Test
    fun `not enough input`() {
        parser.parse(Input("")) shouldEqual null
        parser.parse(Input("f")) shouldEqual null
        parser.parse(Input("foo", offset = 1)) shouldEqual null
    }

    @Test
    fun `input mismatch`() {
        parser.parse(Input("bar")) shouldEqual null
        parser.parse(Input("fo0")) shouldEqual null
    }

    @Test
    fun `input match`() {
        parser.parse(Input("foo")) shouldEqual Output("foo", Input("foo", offset = 3))
        parser.parse(Input("foo__")) shouldEqual Output("foo", Input("foo__", offset = 3))
        parser.parse(Input("_foo_", offset = 1)) shouldEqual Output("foo", Input("_foo_", offset = 4))
        parser.parse(Input("__foo", offset = 2)) shouldEqual Output("foo", Input("__foo", offset = 5))
    }
}

class CharTests {
    val parser = char('a')

    @Test
    fun `not enough input`() {
        parser.parse(Input("")) shouldEqual null
    }

    @Test
    fun `input mismatch`() {
        parser.parse(Input("b")) shouldEqual null
        parser.parse(Input("ab", offset = 1)) shouldEqual null
    }

    @Test
    fun `input match`() {
        parser.parse(Input("a")) shouldEqual Output('a', Input("a", offset = 1))
        parser.parse(Input("a__")) shouldEqual Output('a', Input("a__", offset = 1))
        parser.parse(Input("_a_", offset = 1)) shouldEqual Output('a', Input("_a_", offset = 2))
        parser.parse(Input("__a", offset = 2)) shouldEqual Output('a', Input("__a", offset = 3))
    }
}

class AnyCharTests {
    private val parser = anyCharExcept('x')

    @Test
    fun `not enough input`() {
        parser.parse(Input("")) shouldEqual null
    }

    @Test
    fun `input mismatch`() {
        parser.parse(Input("x")) shouldEqual null
        parser.parse(Input("ax", offset = 1)) shouldEqual null
    }

    @Test
    fun `input match`() {
        parser.parse(Input("a")) shouldEqual Output('a', Input("a", offset = 1))
        parser.parse(Input("a__")) shouldEqual Output('a', Input("a__", offset = 1))
        parser.parse(Input("_a_", offset = 1)) shouldEqual Output('a', Input("_a_", offset = 2))
        parser.parse(Input("__a", offset = 2)) shouldEqual Output('a', Input("__a", offset = 3))
    }
}
