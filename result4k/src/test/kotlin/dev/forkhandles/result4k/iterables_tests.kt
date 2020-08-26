package dev.forkhandles.result4k

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AllValuesTests {
    @Test
    fun `returns values of an iterable of results, if all are success`() {
        assertEquals(Success(listOf(1, 2, 3)), listOf(Success(1), Success(2), Success(3)).allValues())
    }

    @Test
    fun `returns first failure encountered, if not all are success`() {
        assertEquals(Failure("bad"), listOf(Success(1), Failure("bad"), Success(3)).allValues())
    }
}

class AnyValuesTests {
    @Test
    fun `returns any values of an iterable of results, dropping failures`() {
        assertEquals(listOf(1, 2, 3), listOf(Success(1), Success(2), Success(3)).anyValues())
        assertEquals(listOf(1, 3), listOf(Success(1), Failure("bad"), Success(3)).anyValues())
        assertEquals(emptyList(), listOf(Failure("bad")).anyValues())
    }
}

class PartitionTests {
    @Test
    fun `returns values and failures in separate lists`() {
        assertEquals(Pair(listOf(1, 3), listOf("bad", "also bad")),
            listOf(Success(1), Failure("bad"), Success(3), Failure("also bad")).partition())
        assertEquals(Pair(listOf(1, 2, 3), emptyList()),
            listOf(Success(1), Success(2), Success(3)).partition())
        assertEquals(Pair(emptyList(), listOf("bad", "also bad")),
            listOf(Failure("bad"), Failure("also bad")).partition())
    }
}
