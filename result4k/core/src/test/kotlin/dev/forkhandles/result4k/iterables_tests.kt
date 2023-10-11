package dev.forkhandles.result4k

import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertEquals

class AllValuesTests {
    @Test
    fun `returns values of results, if all are success`() {
        assertEquals(
            Success(listOf(1, 2, 3)),
            listOf(Success(1), Success(2), Success(3)).allValues()
        )
    }

    @Test
    fun `returns first failure encountered, if not all are success`() {
        assertEquals(
            Failure("bad"),
            listOf(Success(1), Failure("bad"), Success(3)).allValues()
        )
    }
}

class AllValuesSequenceTests {
    @Test
    fun `returns values of results, if all are success`() {
        assertEquals(
            Success(listOf(1, 2, 3)),
            sequenceOf(Success(1), Success(2), Success(3)).allValues()
        )
    }

    @Test
    fun `returns first failure encountered, if not all are success`() {
        assertEquals(
            Failure("bad"),
            sequenceOf(Success(1), Failure("bad"), Success(3)).allValues()
        )
    }
}

class AnyValuesTests {
    @Test
    fun `returns any values of an iterable of results, dropping failures`() {
        assertEquals(
            listOf(1, 2, 3),
            listOf(Success(1), Success(2), Success(3)).anyValues()
        )
        assertEquals(
            listOf(1, 3),
            listOf(Success(1), Failure("bad"), Success(3)).anyValues()
        )
        assertEquals(
            emptyList(),
            listOf(Failure("bad")).anyValues()
        )
    }
}

class AnyValuesSequenceTests {
    @Test
    fun `returns any values of an iterable of results, dropping failures`() {
        assertEquals(
            listOf(1, 2, 3),
            sequenceOf(Success(1), Success(2), Success(3)).anyValues().toList()
        )
        assertEquals(
            listOf(1, 3),
            sequenceOf(Success(1), Failure("bad"), Success(3)).anyValues().toList()
        )
        assertEquals(
            emptyList(),
            sequenceOf(Failure("bad")).anyValues().toList()
        )
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

private fun randomPositiveInt() = Random.nextInt(1, 100)

private fun generateRandomList(): List<Int> =
    listOf(
        randomPositiveInt(),
        randomPositiveInt(),
        randomPositiveInt(),
        randomPositiveInt(),
        randomPositiveInt()
    )

class TraverseIterableTests {
    @Test
    fun `returns the first failure or the folded iterable as success`() {
        val list = generateRandomList()
        assertEquals(Success(list.sum()),
            list.foldResult(Success(0)) { acc: Int, i: Int -> Success(acc + i) })
    }

    @Test
    fun `returns the first failure or the mapping of iterable as success`() {
        val list = generateRandomList()
        assertEquals(Success(list.map { it * 2 }),
            list.mapAllValues { i -> Success(i * 2) })
    }

    @Test
    fun `failure is returned if the folding operation fails`() {
        val list = generateRandomList()
        fun failingOperation(a: Int, b: Int) = Failure("Test error")

        assertEquals(Failure("Test error"),
            list.foldResult(Success(100), ::failingOperation))
    }

    @Test
    fun `failure is returned if the mapping operation fails`() {
        val list = generateRandomList()
        val failingFunction: (Int) -> Result<Int, String> = { _ -> Failure("Test error") }
        assertEquals(Failure("Test error"),
            list.mapAllValues(failingFunction))
    }
}

class TraverseSequenceTests {

    @Test
    fun `returns the first failure or the folded sequence as success`() {
        val list = generateRandomList()
        val sequence = list.asSequence()
        assertEquals(Success(list.sum()),
            sequence.foldResult(Success(0)) { acc, i -> Success(acc + i) })
    }

    @Test
    fun `returns the first failure or the mapping of sequence as success`() {
        val list = generateRandomList()
        val sequence = list.asSequence()
        assertEquals(Success(list.map { it * 2 }),
            sequence.mapAllValues { i -> Success(i * 2) })
    }

    @Test
    fun `failure is returned if the folding operation fails`() {
        val sequence = generateSequence { randomPositiveInt() }.take(5)
        val failingOperation: (Int, Int) -> Result<Int, String> = { _, _ -> Failure("Test error") }
        assertEquals(Failure("Test error"),
            sequence.foldResult(Success(0), failingOperation))
    }

    @Test
    fun `failure is returned if the mapping operation fails`() {
        val sequence = generateSequence { randomPositiveInt() }.take(5)
        val failingFunction: (Int) -> Result<Int, String> = { _ -> Failure("Test error") }
        assertEquals(Failure("Test error"),
            sequence.mapAllValues(failingFunction))
    }
}
