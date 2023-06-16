package dev.forkhandles.result4k

import org.junit.jupiter.api.Test
import kotlin.random.Random
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
    fun `test foldResult for Iterable`() {
        val list = generateRandomList()
        assertEquals(Success(list.sum()),
            list.foldResult(Success(0)) { acc: Int, i: Int -> Success(acc + i) })
    }

    @Test
    fun `test traverse for Iterable`() {
        val list = generateRandomList()
        assertEquals(Success(list.map { it * 2 }),
            list.traverse { i -> Success(i * 2) })
    }

    @Test
    fun `test extractList for Iterable`() {
        val list = generateRandomList().map { Success(it) }
        assertEquals(Success(list.map { it.value }),
            list.extractList())
    }

    @Test
    fun `test foldResult with failure for Iterable`() {
        val list = generateRandomList()
        fun failingOperation(a: Int, b: Int) = Failure("Test error")

        assertEquals(Failure("Test error"),
            list.foldResult(Success(100), ::failingOperation))
    }

    @Test
    fun `test traverse with failure for Iterable`() {
        val list = generateRandomList()
        val failingFunction: (Int) -> Result<Int, String> = { _ -> Failure("Test error") }
        assertEquals(Failure("Test error"), list.traverse(failingFunction))
    }

    @Test
    fun `test extractList with failure for Iterable`() {
        val list = listOf(Success(randomPositiveInt()), Failure("Test error"))
        assertEquals(Failure("Test error"), list.extractList())
    }
}

class TraverseSequenceTests {

    @Test
    fun `test foldResult for Sequence`() {
        val list = generateRandomList()
        val sequence = list.asSequence()
        assertEquals(Success(list.sum()),
            sequence.foldResult(Success(0)) { acc, i -> Success(acc + i) })
    }

    @Test
    fun `test traverse for Sequence`() {
        val list = generateRandomList()
        val sequence = list.asSequence()
        assertEquals(Success(list.map { it * 2 }),
            sequence.traverse { i -> Success(i * 2) })
    }

    @Test
    fun `test extractList for Sequence`() {
        val list = generateRandomList()
        val sequence = list.map { Success(it) }.asSequence()
        assertEquals(Success(list), sequence.extractList())
    }

    @Test
    fun `test foldResult with failure for Sequence`() {
        val sequence = generateSequence { randomPositiveInt() }.take(5)
        val failingOperation: (Int, Int) -> Result<Int, String> = { _, _ -> Failure("Test error") }
        assertEquals(Failure("Test error"),
            sequence.foldResult(Success(0), failingOperation))
    }

    @Test
    fun `test traverse with failure for Sequence`() {
        val sequence = generateSequence { randomPositiveInt() }.take(5)
        val failingFunction: (Int) -> Result<Int, String> = { _ -> Failure("Test error") }
        assertEquals(Failure("Test error"),
            sequence.traverse(failingFunction))
    }

    @Test
    fun `test extractList with failure for Sequence`() {
        val sequence = generateSequence { Success(randomPositiveInt()) }.take(5) + sequenceOf(Failure("Test error"))
        assertEquals(Failure("Test error"), sequence.extractList())
    }
}
