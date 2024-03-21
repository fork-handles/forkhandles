package dev.forkhandles.state4k

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import dev.forkhandles.result4k.Success
import dev.forkhandles.state4k.StateTransitionResult.IllegalCommand
import dev.forkhandles.state4k.StateTransitionResult.IllegalEvent
import dev.forkhandles.state4k.StateTransitionResult.OK
import dev.forkhandles.state4k.StateTransitionResultTest.SimpleCommand.aCommand
import dev.forkhandles.state4k.StateTransitionResultTest.SimpleState.four
import dev.forkhandles.state4k.StateTransitionResultTest.SimpleState.one
import dev.forkhandles.state4k.StateTransitionResultTest.SimpleState.three
import dev.forkhandles.state4k.StateTransitionResultTest.SimpleState.two
import org.junit.jupiter.api.Test

class StateTransitionResultTest {
    private val originalEntity = SimpleEntity(one, "test")

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


    // the lens gets and sets the state on the Entity
    val lens = StateIdLens(SimpleEntity::state) { entity, state -> entity.copy(state = state) }

    // the commands is responsible for issuing new commands to process the machine
    val commands = Commands<SimpleEntity, SimpleCommand, String> { _: SimpleEntity, _ -> Success(Unit) }

    // define the machine
    val simpleStateMachine = StateMachine<SimpleState, SimpleEntity, SimpleEvent, SimpleCommand, String>(
        commands,
        lens,
        // define the state transitions for state one
        StateBuilder<SimpleState, SimpleEntity, SimpleCommand>(one)
            .transition<SimpleEvent1>(two) { e, o -> o.copy(lastAction = "received $e") },

        // define the state transitions for state two
        StateBuilder<SimpleState, SimpleEntity, SimpleCommand>(two)
            .onEnter(aCommand)
            .transition<SimpleEvent2>(three) { e, o -> o.copy(lastAction = "received $e") }
            .transition<SimpleEvent3>(four) { e, o -> o.copy(lastAction = "received $e") }
    )

    data class SimpleEntity(val state: SimpleState, val lastAction: String)

    enum class SimpleState {
        one, two, three, four
    }

    interface SimpleEvent

    data object SimpleEvent1 : SimpleEvent
    data object SimpleEvent2 : SimpleEvent
    data object SimpleEvent3 : SimpleEvent

    enum class SimpleCommand {
        aCommand
    }
}
