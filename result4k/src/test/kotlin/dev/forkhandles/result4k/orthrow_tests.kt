package dev.forkhandles.result4k

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.lang.Exception
import kotlin.test.assertEquals

class OrThrowTests {
    private class ExampleException : Exception()
    
    @Test
    fun `success returns value`() {
        val r: Result<String, ExampleException> = Success("x")
        
        assertEquals("x", r.orThrow())
    }
    
    @Test
    fun `failure is thrown if an exception`() {
        val r: Result<String, ExampleException> = Failure(ExampleException())
        
        assertThrows<ExampleException> { r.orThrow() }
    }
}