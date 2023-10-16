package dev.forkhandles.result4k

import org.junit.jupiter.api.Test
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

class PartitionIterableTests : PartitionTests({ partition() })
class PartitionSequenceTests : PartitionTests({ asSequence().partition() })

abstract class FoldResultTests(
    private val foldResult: List<Int>.(Result<Int, *>, (Int, Int) -> Result<Int, *>) -> Result<*, *>
) {
    @Test
    fun `returns the first failure or the folded values as success`() {
        assertEquals(
            Success(0),
            emptyList<Int>().foldResult(Success(0)) { _, _ -> Failure("bad") }
        )
        assertEquals(
            Success(listOf(1, 2, 3).sum()),
            listOf(1, 2, 3).foldResult(Success(0)) { acc, i -> Success(acc + i) }
        )
        assertEquals(
            Failure("bad"),
            listOf(1, 2, 3).foldResult(Success(0)) { _, _ -> Failure("bad") }
        )
    }
}

class FoldResultIterableTests : FoldResultTests({ initial, operation -> foldResult(initial, operation) })
class FoldResultSequenceTests : FoldResultTests({ initial, operation -> asSequence().foldResult(initial, operation) })

abstract class MapAllValuesTests(
    private val mapAllValues: List<Int>.((Int) -> Result<*, *>) -> Result<*, *>
) {
    @Test
    fun `returns the first failure or the mapping of values as success`() {
        assertEquals(
            Success(emptyList<Int>()),
            emptyList<Int>().mapAllValues { Failure("bad") }
        )
        assertEquals(
            Success(listOf(1, 2, 3).map { it * 2 }),
            listOf(1, 2, 3).mapAllValues { Success(it * 2) }
        )
        assertEquals(
            Failure("bad"),
            listOf(1, 2, 3).mapAllValues { Failure("bad") }
        )
    }
}

class MapAllValuesIterableTests : MapAllValuesTests({ mapAllValues(it) })
class MapAllValuesSequenceTests : MapAllValuesTests({ asSequence().mapAllValues(it) })
