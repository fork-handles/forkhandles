package dev.forkhandles.state4k

import dev.forkhandles.result4k.Result
import dev.forkhandles.result4k.Success
import dev.forkhandles.result4k.flatMap
import dev.forkhandles.result4k.map
import dev.forkhandles.state4k.StateTransitionResult.IllegalCommand
import dev.forkhandles.state4k.StateTransitionResult.IllegalEvent
import dev.forkhandles.state4k.StateTransitionResult.OK

/**
 * Standard state machine pattern. Build with a list of state definitions and transition over them with
 * events. Each transition can create a new command to be issued.
 */
class StateMachine<State, Entity, Event : Any, Command, Error>(
    private val commands: Commands<Entity, Command, Error>,
    private val stateLens: EntityStateLens<Entity, State>,
    private val states: List<MachineState<State, Entity, Command>>,
) {
    constructor(
        commands: Commands<Entity, Command, Error>,
        entityStateLens: EntityStateLens<Entity, State>,
        vararg stateBuilders: StateBuilder<State, Entity, Command>
    ) : this(commands,
        entityStateLens,
        stateBuilders.map { MachineState(it.start, it.onEnter, it.transitions) }
    )

    /**
     * Transition the entity by checking then running a command, and applying the resultant event to the entity
     */
    fun <Next : Event> transition(entity: Entity, command: Command, toEvent: (Entity) -> Result<Next, Error>)
        : Result<StateTransitionResult<State, Entity, Command>, Error> =
        when {
            states.any { it.name == stateLens(entity) && it.onEnter == command } ->
                toEvent(entity).flatMap { transition(entity, it) }

            else -> Success(IllegalCommand(entity, command))
        }

    /**
     * Transition the entity by applying a command
     */
    @Suppress("UNCHECKED_CAST")
    fun <Next : Event> transition(entity: Entity, event: Next)
        : Result<StateTransitionResult<State, Entity, Command>, Error> {
        val transition = states
            .firstOrNull { it.name == stateLens(entity) }
            ?.let { state ->
                state.transitions.firstOrNull { event::class == it.event }
                    ?.let { it as? StateTransition<State, Entity, Next> ?: error("Illegal state transition") }
            }

        return transition
            ?.let {
                (states.firstOrNull { state -> state.name == it.end }
                    ?.onEnter?.let { commands(entity, it) } ?: Success(Unit))
                    .also { println(it) }
                    .map { OK(stateLens(transition.applyTo(event, entity), transition.end)) }
            } ?: Success(IllegalEvent(entity, event))
    }

    /**
     * Render the state machine using the passed renderer
     */
    fun renderUsing(renderer: StateMachineRenderer) = renderer(states)
}

data class MachineState<STATE, ENTITY, COMMAND>(
    val name: STATE,
    val onEnter: COMMAND?,
    val transitions: List<StateTransition<STATE, ENTITY, *>>
)
