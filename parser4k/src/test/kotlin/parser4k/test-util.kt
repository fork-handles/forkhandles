package parser4k

import org.junit.jupiter.api.Assertions.assertEquals


@IgnorableReturnValue
infix fun Any?.shouldEqual(expected: Any?) =
    assertEquals(expected, this)

@IgnorableReturnValue
infix fun (() -> Any?).shouldFailWith(f: (ParsingError) -> Boolean) =
    try {
        this()
    } catch (e: ParsingError) {
        assert(f(e))
    }

@IgnorableReturnValue
infix fun (() -> Any?).shouldFailWithMessage(expectedMessage: String) =
    try {
        this()
    } catch (e: ParsingError) {
        e.message shouldEqual expectedMessage.trimMargin()
    }
