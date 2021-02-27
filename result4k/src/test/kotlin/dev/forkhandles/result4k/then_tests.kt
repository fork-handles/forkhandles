package dev.forkhandles.result4k

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.tuples.Tuple2
import dev.forkhandles.tuples.Tuple3
import dev.forkhandles.tuples.Tuple4
import dev.forkhandles.tuples.Tuple5
import dev.forkhandles.tuples.tuple
import org.junit.jupiter.api.Test

class ThenTests {

    private val _1 = Success(1)
    private val _2 = Success(2)
    private val _3 = Success(3)
    private val _4 = Success(4)
    private val _5 = Success(5)
    private val _6 = Success(6)

    private val fail = Failure(RuntimeException())

    @Test
    fun then() {
        assertThat(
            _1
                .then1 { _2 }
                .then2 { _3 }
                .then3 { _4 }
                .then4 { _5 }
                .then5 { _6 }
                .orThrow(), equalTo(tuple(1, 2, 3, 4, 5, 6)))

        assertThat(
            _1
                .then1 { _2 }
                .then2 { _3 }
                .then3 { fail }
                .then4 { _5 }
                .then5 { _6 }
                .failureOrNull(), equalTo(fail.reason)
        )
    }
}
