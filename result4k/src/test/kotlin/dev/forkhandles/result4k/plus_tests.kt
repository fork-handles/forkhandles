package dev.forkhandles.result4k

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.tuples.tuple
import org.junit.jupiter.api.Test

class PlusTests {

    private val _1 = Success(1)
    private val _2 = Success(2)
    private val _3 = Success(3)
    private val _4 = Success(4)
    private val _5 = Success(5)
    private val _6 = Success(6)

    private val fail = Failure(RuntimeException())

    @Test
    fun plus() {
        assertThat((_1 + _2).orThrow(), equalTo(tuple(1, 2)))
        assertThat((_1 + fail).failureOrNull(), equalTo(fail.reason))
    }

    @Test
    fun `plus 2`() {
        assertThat((_1 + _2 + _3).orThrow(), equalTo(tuple(1, 2, 3)))
        assertThat((_1 + _2 + fail).failureOrNull(), equalTo(fail.reason))
    }

    @Test
    fun `plus 3`() {
        assertThat((_1 + _2 + _3 + _4).orThrow(), equalTo(tuple(1, 2, 3, 4)))
        assertThat((_1 + _2 + _3 + fail).failureOrNull(), equalTo(fail.reason))
    }

    @Test
    fun `plus 4`() {
        assertThat((_1 + _2 + _3 + _4 + _5).orThrow(), equalTo(tuple(1, 2, 3, 4, 5)))
        assertThat((_1 + _2 + _3 + _4 + fail).failureOrNull(), equalTo(fail.reason))
    }

    @Test
    fun `plus 5`() {
        assertThat((_1 + _2 + _3 + _4 + _5 + _6).orThrow(), equalTo(tuple(1, 2, 3, 4, 5, 6)))
        assertThat((_1 + _2 + _3 + _4 + _5 + fail).failureOrNull(), equalTo(fail.reason))
    }
}
