package dev.forkhandles.state4k

/**
 * The various outcomes which can happen out of a state machine transition
 */
sealed interface StateTransitionResult<State, Entity, CommandType> {
    val entity: Entity

    /**
     * This transition was valid and the updated entity is enclosed in the field
     */
    data class OK<State, Entity, CommandType>(override val entity: Entity) :
        StateTransitionResult<State, Entity, CommandType>

    /**
     * This transition via an event was not valid. The unmodified entity is enclosed in the field.
     */
    data class IllegalEvent<State, Entity, CommandType>(
        override val entity: Entity,
        val event: Any
    ) :
        StateTransitionResult<State, Entity, CommandType>

    /**
     * This transition via an command was not valid. The unmodified entity is enclosed in the field.
     */
    data class IllegalCommand<State, Entity, CommandType>(
        override val entity: Entity,
        val commandType: CommandType
    ) :
        StateTransitionResult<State, Entity, CommandType>
}
