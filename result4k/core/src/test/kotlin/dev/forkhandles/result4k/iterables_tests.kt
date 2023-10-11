package dev.forkhandles.result4k

import org.junit.jupiter.api.Test
import kotlin.random.Random
import kotlin.test.assertEquals

abstract class AllValuesTests(
    private val allValues: List<Result<Int, String>>.() -> Result<List<Int>, String>
) {
    @Test
    fun `returns values of all results or the first failure`() {
        assertEquals(
            Success(emptyList()),
            emptyList<Result<Int, String>>().allValues()
        )
        assertEquals(
            Success(listOf(1, 2, 3)),
            listOf(Success(1), Success(2), Success(3)).allValues()
        )
        assertEquals(
            Failure("bad"),
            listOf(Success(1), Failure("bad"), Success(3)).allValues()
        )
        assertEquals(
            Failure("bad"),
            listOf(Success(1), Failure("bad"), Success(3), Failure("also bad")).allValues()
        )
    }
}

class AllValuesIterableTests : AllValuesTests({ allValues() })
class AllValuesSequenceTests : AllValuesTests({ asSequence().allValues() })

abstract class AnyValuesTests(
    private val anyValues: List<Result<Int, String>>.() -> List<Int>
) {
    @Test
    fun `returns any values of an iterable of results, dropping failures`() {
        assertEquals(
            emptyList(),
            emptyList<Result<Int, String>>().anyValues()
        )
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

class AnyValuesIterableTests : AnyValuesTests({ anyValues() })
class AnyValuesSequenceTests : AnyValuesTests({ asSequence().anyValues().toList() })

abstract class PartitionTests(
    private val partition: List<Result<Int, String>>.() -> Pair<List<Int>, List<String>>
) {
    @Test
    fun `returns values and failures in separate lists`() {
        assertEquals(
            Pair(emptyList(), emptyList()),
            emptyList<Result<Int, String>>().partition()
        )
        assertEquals(
            Pair(listOf(1, 2, 3), emptyList()),
            listOf(Success(1), Success(2), Success(3)).partition()
        )
        assertEquals(
            Pair(emptyList(), listOf("bad", "also bad")),
            listOf(Failure("bad"), Failure("also bad")).partition()
        )
        assertEquals(
            Pair(listOf(1, 3), listOf("bad", "also bad")),
            listOf(Success(1), Failure("bad"), Success(3), Failure("also bad")).partition()
        )
    }
}

class PartitionIterableTests: PartitionTests({ partition() })
class PartitionSequenceTests: PartitionTests({ asSequence().partition() })


class TraverseIterableTests {
    @Test
    fun `returns the first failure or the folded iterable as success`() {
        val list = randomList()
        assertEquals(Success(list.sum()),
            list.foldResult(Success(0)) { acc: Int, i: Int -> Success(acc + i) })
    }

    @Test
    fun `returns the first failure or the mapping of iterable as success`() {
        val list = randomList()
        assertEquals(Success(list.map { it * 2 }),
            list.mapAllValues { i -> Success(i * 2) })
    }

    @Test
    fun `failure is returned if the folding operation fails`() {
        assertEquals(Failure("Test error"),
            randomList().foldResult(Success(100)) { _, _ -> Failure("Test error") })
    }

    @Test
    fun `failure is returned if the mapping operation fails`() {
        val list = randomList()
        val failingFunction: (Int) -> Result<Int, String> = { _ -> Failure("Test error") }
        assertEquals(Failure("Test error"),
            list.mapAllValues(failingFunction))
    }
}

class TraverseSequenceTests {

    @Test
    fun `returns the first failure or the folded sequence as success`() {
        val list = randomList()
        val sequence = list.asSequence()
        assertEquals(Success(list.sum()),
            sequence.foldResult(Success(0)) { acc, i -> Success(acc + i) })
    }

    @Test
    fun `returns the first failure or the mapping of sequence as success`() {
        val list = randomList()
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
        assertEquals(Failure("Test error"),
            sequence.mapAllValues { _ -> Failure("Test error") })
    }
}

private fun randomPositiveInt() = Random.nextInt(1, 100)

private fun randomList(): List<Int> =
    listOf(
        randomPositiveInt(),
        randomPositiveInt(),
        randomPositiveInt(),
        randomPositiveInt(),
        randomPositiveInt()
    )
