package dev.forkhandles.tuples

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.jupiter.api.Test

class TupleExampleTest {
    @Test
    fun `appending tuples`() {
        assertThat(tuple(1, 2, 3, 4, 5) + tuple(6, 7, 8), equalTo(tuple(1, 2, 3, 4, 5, 6, 7, 8)))
    }
    
    @Test
    fun `to lists`() {
        assertThat(tuple(1,2,3,4,5).toList(), equalTo(listOf(1,2,3,4,5)))
    }
    
    @Test
    fun `from lists`() {
        val list: List<Int> = listOf(1, 2, 3, 4)
        
        assertThat(list.toTuple2(), equalTo(tuple<Int?,Int?>(1,2)))
        assertThat(list.toTuple3(), equalTo(tuple<Int?,Int?,Int?>(1,2,3)))
        assertThat(list.toTuple4(), equalTo(tuple<Int?,Int?,Int?,Int?>(1,2,3,4)))
        assertThat(list.toTuple5(), equalTo(tuple<Int?,Int?,Int?,Int?,Int?>(1, 2, 3, 4, null)))
        assertThat(list.toTuple6(), equalTo(tuple<Int?,Int?,Int?,Int?,Int?,Int?>(1,2,3,4,null,null)))
    }
    
    @Test
    fun `null to non-null`() {
        val t : Tuple4<Int?,Int?,Int?,Int?> = tuple(1,2,3,4)
        
        assertThat(t.allNonNull(), equalTo(t))
        assertThat(t.copy(val2=null).allNonNull(), absent())
    }
}
