package dev.forkhandles.state4k

import dev.forkhandles.result4k.Success
import dev.forkhandles.state4k.SimpleCommand.aCommand
import dev.forkhandles.state4k.SimpleState.four
import dev.forkhandles.state4k.SimpleState.one
import dev.forkhandles.state4k.SimpleState.three
import dev.forkhandles.state4k.SimpleState.two
import dev.forkhandles.state4k.render.Puml
import kotlin.random.Random

// the lens gets and sets the state on the Entity
val lens = EntityStateLens(SimpleEntity::state) { entity, state -> entity.copy(state = state) }

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

fun main() {
    // returns OK with the updated entity, and the aCommand is issued and sent
    val update1 = simpleStateMachine.transition(
        SimpleEntity(one, ""),
        SimpleEvent1
    )
    // returns "illegal transition" - no transition is made and no commands sent
    val failed = simpleStateMachine.transition(
        SimpleEntity(one, ""),
        SimpleEvent2
    )

    // returns OK with the updated entity in state three or four
    val update2 = simpleStateMachine.transition(SimpleEntity(two, ""), aCommand) {
        // imagine a remote operation here which could go one of 2 ways (or fail!)
        when (Random.nextBoolean()) {
            true -> Success(SimpleEvent2)
            false -> Success(SimpleEvent3)
        }
    }

    println(simpleStateMachine.renderUsing(Puml("simple")))
}
