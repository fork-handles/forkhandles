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
    private val stateTransitions: List<StateTransition<State, Entity, *, Command>> = emptyList()
) {
    constructor(
        commands: Commands<Entity, Command, Error>,
        entityStateLens: EntityStateLens<Entity, State>,
        vararg stateBuilders: StateBuilder<State, Entity, Command>
    ) : this(commands, entityStateLens, stateBuilders.flatMap { state -> state.transitions })

    /**
     * Transition the entity by checking then running a command, and applying the resultant event to the entity
     */
    fun <Next : Event> transition(entity: Entity, command: Command, toEvent: (Entity) -> Result<Next, Error>)
        : Result<StateTransitionResult<State, Entity, Command>, Error> =
        when {
            stateTransitions.any { it.end == stateLens(entity) && it.nextCommand == command } ->
                toEvent(entity).flatMap { transition(entity, it) }

            else -> Success(IllegalCommand(entity, command))
        }

    /**
     * Transition the entity by applying a command
     */
    @Suppress("UNCHECKED_CAST")
    fun <Next : Event> transition(entity: Entity, event: Next)
        : Result<StateTransitionResult<State, Entity, Command>, Error> =
        stateTransitions.firstOrNull { stateLens(entity) == it.start && event::class == it.event }
            ?.let { transition ->
                transition as? StateTransition<State, Entity, Next, Command> ?: error("Illegal state transition")
                (transition.nextCommand?.let { commands(entity, it) } ?: Success(Unit))
                    .map { OK(stateLens(transition.applyTo(event, entity), transition.end)) }
            } ?: Success(IllegalEvent(entity, event))

    /**
     * Render the state machine using the passed renderer
     */
    fun renderUsing(renderer: StateMachineRenderer) = renderer(stateTransitions)
}
