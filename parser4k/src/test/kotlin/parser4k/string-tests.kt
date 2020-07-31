package parser4k

import org.junit.jupiter.api.Test

class StringTests {
    @Test fun `it works`() {
        val parser = str("foo")

        // not enough input
        parser.invoke(Input("")) shouldEqual null
        parser.invoke(Input("f")) shouldEqual null
        parser.invoke(Input("foo", offset = 1)) shouldEqual null

        // input mismatch
        parser.invoke(Input("bar")) shouldEqual null
        parser.invoke(Input("fo0")) shouldEqual null

        // match
        parser.invoke(Input("foo")) shouldEqual Output("foo", Input("foo", offset = 3))
        parser.invoke(Input("foo__")) shouldEqual Output("foo", Input("foo__", offset = 3))
        parser.invoke(Input("_foo_", offset = 1)) shouldEqual Output("foo", Input("_foo_", offset = 4))
        parser.invoke(Input("__foo", offset = 2)) shouldEqual Output("foo", Input("__foo", offset = 5))
    }
}
