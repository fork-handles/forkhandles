package dev.forkhandles.state4k

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.state4k.SimpleCommand.aCommand
import dev.forkhandles.state4k.StateTransitionResult.IllegalCommand
import dev.forkhandles.state4k.StateTransitionResult.IllegalEvent
import dev.forkhandles.state4k.StateTransitionResult.OK
import org.junit.jupiter.api.Test

class StateTransitionResultTest {
    private val originalEntity = SimpleEntity(SimpleState.one, "test")

    @Test
    fun `map over an OK`() {
        val original = OK<SimpleState, SimpleEntity, SimpleCommand>(originalEntity)
            .map { it.copy(lastAction = "foo") }

        assertThat(original.entity.lastAction, equalTo("foo"))
    }

    @Test
    fun `map over an illegal command`() {
        val original = IllegalCommand<SimpleState, SimpleEntity, SimpleCommand>(originalEntity, aCommand)
            .map { it.copy(lastAction = "foo") }

        assertThat(original.entity.lastAction, equalTo("foo"))
    }

    @Test
    fun `map over an illegal event`() {
        val original = IllegalEvent<SimpleState, SimpleEntity, SimpleCommand>(originalEntity, SimpleEvent2)
            .map { it.copy(lastAction = "foo") }

        assertThat(original.entity.lastAction, equalTo("foo"))
    }
}
