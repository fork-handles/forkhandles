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
        assertEquals(
            Pair(listOf(1, 3), listOf("bad", "also bad")),
            listOf(Success(1), Failure("bad"), Success(3), Failure("also bad")).partition()
        )
        assertEquals(
            Pair(listOf(1, 2, 3), emptyList()),
            listOf(Success(1), Success(2), Success(3)).partition()
        )
        assertEquals(
            Pair(emptyList(), listOf("bad", "also bad")),
            listOf(Failure("bad"), Failure("also bad")).partition()
        )
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
        val result = list.foldResult(Success(0)) { acc: Int, i: Int -> Success(acc + i) }
        val expected = Success(list.sum())
        assertEquals(expected, result)
    }

    @Test
    fun `test traverse for Iterable`() {
        val list = generateRandomList()
        val result = list.traverse { i -> Success(i * 2) }
        val expected = Success(list.map { it * 2 })
        assertEquals(expected, result)
    }

    @Test
    fun `test extractList for Iterable`() {
        val list = generateRandomList().map { Success(it) }
        val result = list.extractList()
        val expected = Success(list.map { it.value })
        assertEquals(expected, result)
    }

    @Test
    fun `test foldResult with failure for Iterable`() {
        val list = generateRandomList()
        fun failingOperation(a: Int, b: Int) = Failure("Test error")

        val result = list.foldResult(Success(100), ::failingOperation)
        val expected = Failure("Test error")
        assertEquals(expected, result)
    }



    @Test
    fun `test traverse with failure for Iterable`() {
        val list = generateRandomList()
        val failingFunction: (Int) -> Result<Int, String> = { _ -> Failure("Test error") }
        val result = list.traverse(failingFunction)
        val expected = Failure("Test error")
        assertEquals(expected, result)
    }

    @Test
    fun `test extractList with failure for Iterable`() {
        val list = listOf(Success(randomPositiveInt()), Failure("Test error"))
        val result = list.extractList()
        val expected = Failure("Test error")
        assertEquals(expected, result)
    }

}

class TraverseSequenceTests {

    @Test
    fun `test foldResult for Sequence`() {
        val list = generateRandomList()
        val sequence = list.asSequence()
        val result = sequence.foldResult(Success(0)) { acc, i -> Success(acc + i) }
        val expected = Success(list.sum())
        assertEquals(expected, result)
    }

    @Test
    fun `test traverse for Sequence`() {
        val list = generateRandomList()
        val sequence = list.asSequence()
        val result = sequence.traverse { i -> Success(i * 2) }
        val expected = Success(list.map { it * 2 })
        assertEquals(expected, result)
    }

    @Test
    fun `test extractList for Sequence`() {

        val list = generateRandomList()
        val sequence = list.map { Success(it) }.asSequence()

        val result = sequence.extractList()
        val expected = Success(list)
        assertEquals(expected, result)
    }

    @Test
    fun `test foldResult with failure for Sequence`() {
        val sequence = generateSequence { randomPositiveInt() }.take(5)
        val failingOperation: (Int, Int) -> Result<Int, String> = { _, _ -> Failure("Test error") }
        val result = sequence.foldResult(Success(0), failingOperation)
        val expected = Failure("Test error")
        assertEquals(expected, result)
    }

    @Test
    fun `test traverse with failure for Sequence`() {
        val sequence = generateSequence { randomPositiveInt() }.take(5)
        val failingFunction: (Int) -> Result<Int, String> = { _ -> Failure("Test error") }
        val result = sequence.traverse(failingFunction)
        val expected = Failure("Test error")
        assertEquals(expected, result)
    }

    @Test
    fun `test extractList with failure for Sequence`() {
        val sequence = generateSequence { Success(randomPositiveInt()) }.take(5) + sequenceOf(Failure("Test error"))
        val result = sequence.extractList()
        val expected = Failure("Test error")
        assertEquals(expected, result)
    }
}
