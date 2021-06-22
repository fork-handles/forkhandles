package dev.forkhandles.values

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import com.natpryce.hamkrest.greaterThan
import com.natpryce.hamkrest.lessThan
import org.junit.jupiter.api.Test

class ComparatorsTest {
    class IncomparableValue private constructor(value: String) :
        AbstractValue<String>(value)
    {
        companion object : StringValueFactory<IncomparableValue>(::IncomparableValue)
    }
    
    @Test
    fun `comparator of natural order`() {
        val valueA = IncomparableValue.of("alice")
        val valueB = IncomparableValue.of("bob")
    
        val comparator : Comparator<IncomparableValue> =
            compareByValue()
    
        assertThat(comparator.compare(valueA, valueB), lessThan(0))
        assertThat(comparator.compare(valueA, valueA), equalTo(0))
        assertThat(comparator.compare(valueB, valueA), greaterThan(0))
    }
    
    @Test
    fun `comparator of specified order`() {
        val valueA = IncomparableValue.of("alice")
        val valueB = IncomparableValue.of("bob")
    
        val comparator : Comparator<IncomparableValue> =
            compareByValue(reverseOrder())
    
        assertThat(comparator.compare(valueA, valueB), greaterThan(0))
        assertThat(comparator.compare(valueA, valueA), equalTo(0))
        assertThat(comparator.compare(valueB, valueA), lessThan(0))
    }
}
