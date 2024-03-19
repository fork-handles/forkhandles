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
class StateMachine<StateId, Entity, Event : Any, Command, Error>(
    private val commands: Commands<Entity, Command, Error>,
    private val stateLens: StateIdLens<Entity, StateId>,
    private val states: List<State<StateId, Entity, Command>>,
) {
    constructor(
        commands: Commands<Entity, Command, Error>,
        stateIdLens: StateIdLens<Entity, StateId>,
        vararg stateBuilders: StateBuilder<StateId, Entity, Command>
    ) : this(commands,
        stateIdLens,
        stateBuilders.map { State(it.start, it.onEnter, it.transitions) }
    )

    /**
     * Transition the entity by checking then running a command, and applying the resultant event to the entity
     */
    fun <Next : Event> transition(entity: Entity, command: Command, toEvent: (Entity) -> Result<Next, Error>)
        : Result<StateTransitionResult<StateId, Entity, Command>, Error> =
        when {
            states.any { it.id == stateLens(entity) && it.onEnter == command } ->
                toEvent(entity).flatMap { transition(entity, it) }

            else -> Success(IllegalCommand(entity, command))
        }

    /**
     * Transition the entity by applying a command
     */
    @Suppress("UNCHECKED_CAST")
    fun <Next : Event> transition(entity: Entity, event: Next)
        : Result<StateTransitionResult<StateId, Entity, Command>, Error> {
        val transition = states
            .firstOrNull { it.id == stateLens(entity) }
            ?.let { state ->
                state.transitions.firstOrNull { event::class == it.event }
                    ?.let { it as? StateTransition<StateId, Entity, Next> ?: error("Illegal state transition") }
            }

        return transition
            ?.let {
                (states.firstOrNull { state -> state.id == it.end }
                    ?.onEnter?.let { commands(entity, it) } ?: Success(Unit))
                    .map { OK(stateLens(transition.applyTo(event, entity), transition.end)) }
            } ?: Success(IllegalEvent(entity, event))
    }

    /**
     * Render the state machine using the passed renderer
     */
    fun renderUsing(renderer: StateMachineRenderer) = renderer(states)
}

data class State<StateId, Entity, Command>(
    val id: StateId,
    val onEnter: Command?,
    val transitions: List<StateTransition<StateId, Entity, *>>
)
