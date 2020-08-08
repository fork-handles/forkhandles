package parser4k

import org.junit.jupiter.api.Test

class StringTests {
    @Test fun `it works`() {
        val parser = str("foo")

        // not enough input
        parser.parse(Input("")) shouldEqual null
        parser.parse(Input("f")) shouldEqual null
        parser.parse(Input("foo", offset = 1)) shouldEqual null

        // input mismatch
        parser.parse(Input("bar")) shouldEqual null
        parser.parse(Input("fo0")) shouldEqual null

        // match
        parser.parse(Input("foo")) shouldEqual Output("foo", Input("foo", offset = 3))
        parser.parse(Input("foo__")) shouldEqual Output("foo", Input("foo__", offset = 3))
        parser.parse(Input("_foo_", offset = 1)) shouldEqual Output("foo", Input("_foo_", offset = 4))
        parser.parse(Input("__foo", offset = 2)) shouldEqual Output("foo", Input("__foo", offset = 5))
    }
}
